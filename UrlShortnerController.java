package com.proptiger.data.mvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.core.meta.DisableCaching;
import com.proptiger.core.model.cms.UrlShortner;
import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.data.service.UrlShortnerService;

@Controller
public class UrlShortnerController {

	@Autowired
	private UrlShortnerService urlShortnerService;

	@RequestMapping(value = "data/v1/entity/url-shortener/long-url", method = RequestMethod.GET)
	@ResponseBody
	@DisableCaching 
	public APIResponse getLongUrl(@RequestParam String shortUrl) {
		return new APIResponse(urlShortnerService.getLongUrl(shortUrl));
	}

	@RequestMapping(value = "data/v1/entity/url-shortener/long-to-short", method = RequestMethod.POST)
	@ResponseBody
	public APIResponse setShortUrl(@RequestBody List<UrlShortner> inputList) {
		return new APIResponse(urlShortnerService.createShortUrl(inputList));
	}
}
