package com.proptiger.data.service;

import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.common.hash.Hashing;
import com.proptiger.core.dto.external.FailedUrlDto;
import com.proptiger.core.exception.ProAPIException;
import com.proptiger.core.exception.ResourceNotFoundException;
import com.proptiger.core.model.cms.UrlShortner;
import com.proptiger.core.util.URLUtil;
import com.proptiger.data.repo.UrlShortnerDao;

/**
 * 
 * @author divyanshu
 *
 */
@Service
public class UrlShortnerService {

    private static Logger  logger        = LoggerFactory.getLogger(UrlShortnerService.class);

    private int            NO_OF_RETRIES = 3;

    private String         SUCCESS       = "success";

    private String         FAILED        = "failed";

    @Autowired
    private UrlShortnerDao urlShortnerDao;

    public Map<String, Object> createShortUrl(List<UrlShortner> inputList) {

        Map<String, Object> resultMap = new HashMap<>();
        for (UrlShortner input : inputList) {
            resultMap.putAll(createSingleShortUrl(input));
        }
        return resultMap;
    }

    /**
     * 
     * @param inputUrlShortner
     * @return
     */
    public Map<String, Object> createSingleShortUrl(UrlShortner inputUrlShortner) {
        Map<String, Object> res = new HashMap<>();
        FailedUrlDto fUrlDto = new FailedUrlDto();
        try {
            if (inputUrlShortner.getExpDays() < 0) {
                throw new IllegalArgumentException("Invalid expiry days ");
            }
            String domain = "";
            domain = URLUtil.getDomain(inputUrlShortner.getLongUrl());

            if (domain.contains("makaan")) {
                Timestamp currTime = new Timestamp(System.currentTimeMillis());
                Timestamp expiry = currTime;
                String shortUrl = "";
                boolean isFound = true;
                for (int i = 0; i < NO_OF_RETRIES; i++) {
                    Random rand = new Random();
                    currTime = new Timestamp(System.currentTimeMillis());
                    expiry = currTime;
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(expiry.getTime());
                    cal.add(Calendar.DAY_OF_MONTH, inputUrlShortner.getExpDays());
                    expiry = new Timestamp(cal.getTime().getTime());

                    String inputUrl = inputUrlShortner.getLongUrl() + currTime.toString() + rand.nextInt(1000);
                    shortUrl = Hashing.murmur3_32().hashString(inputUrl, StandardCharsets.UTF_8).toString();
                    // Checks in DB if collisions occurs
                    List<UrlShortner> us = urlShortnerDao.findByShortUrlAndExpiryGreaterThan(shortUrl, currTime);

                    if (us.isEmpty()) {
                        isFound = false;
                        break;
                    }
                }
                if (isFound) {
                    throw new ProAPIException("Unable to create short url");
                }
                UrlShortner urlShortner = new UrlShortner(
                        inputUrlShortner.getLongUrl(),
                        shortUrl,
                        currTime,
                        expiry,
                        0,
                        null,
                        inputUrlShortner.getCampaign());
                urlShortnerDao.save(urlShortner);
                urlShortner.setExpDays(inputUrlShortner.getExpDays());
                res.put(SUCCESS, urlShortner);
            }
            else {
                fUrlDto.setLongUrl(inputUrlShortner.getLongUrl());
                fUrlDto.setMessage("Invalid Domain Name: ");
                res.put(FAILED, fUrlDto);
            }
        }
        catch (URISyntaxException e) {
            // TODO: handle exception
            fUrlDto.setLongUrl(inputUrlShortner.getLongUrl());
            fUrlDto.setMessage("Invalid Url: " + e);
            res.put(FAILED, fUrlDto);
        }
        catch (ProAPIException e) {
            // TODO: handle exception
            fUrlDto.setLongUrl(inputUrlShortner.getLongUrl());
            fUrlDto.setMessage("Unable to create Url: " + e);
            res.put(FAILED, fUrlDto);
        }
        catch (IllegalArgumentException e) {
            // TODO: handle exception
            fUrlDto.setLongUrl(inputUrlShortner.getLongUrl());
            fUrlDto.setMessage("Invalid expiry days " + e);
            res.put(FAILED, fUrlDto);
        }
        catch (Exception e) {
            // TODO: handle exception
            fUrlDto.setLongUrl(inputUrlShortner.getLongUrl());
            fUrlDto.setMessage("Some error occured " + e);
            res.put(FAILED, fUrlDto);
        }
        return res;
    }

    /**
     * 
     * @param shortUrl
     * @return
     */
    public UrlShortner getLongUrl(String shortUrl) {
        Timestamp currTime = new Timestamp(System.currentTimeMillis());
        List<UrlShortner> uShortner = urlShortnerDao.findByShortUrlAndExpiryGreaterThan(shortUrl, currTime);
        if (uShortner.isEmpty()) {
            throw new ResourceNotFoundException("Hash key not available: " + shortUrl);
        }
        uShortner.get(0).setHitCount(uShortner.get(0).getHitCount() + 1);
        uShortner.get(0).setLastHitTime(currTime);
        // update No. of hits
        urlShortnerDao.save(uShortner);
        return (uShortner.get(0));

    }
}
