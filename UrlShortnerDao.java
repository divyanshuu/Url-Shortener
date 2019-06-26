package com.proptiger.data.repo;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.core.model.cms.UrlShortner;


public interface UrlShortnerDao extends JpaRepository<UrlShortner, Integer> {

	public List<UrlShortner> findByShortUrl(String shortUrl);

	public List<UrlShortner> findByShortUrlAndExpiryGreaterThan(String shortUrl, Timestamp t);

}
