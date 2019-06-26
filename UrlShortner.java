package com.proptiger.core.model.cms;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.proptiger.core.model.BaseModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonFilter("fieldFilter")
@Entity
@Table(schema = "cms", name = "url_shortner")
public class UrlShortner extends BaseModel {

    private static final long serialVersionUID = 8838609837098095320L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private int               id;

    @Column(name = "long_url")
    @NotNull
    private String            longUrl;

    @Column(name = "short_url")
    private String            shortUrl;

    @Column(name = "created_at")
    private Timestamp         createdAt;

    @Column(name = "expiry")
    private Timestamp         expiry;

    @Column(name = "hit_count")
    private int               hitCount;

    @Column(name = "last_hit_time")
    @JsonIgnore
    private Timestamp         lastHitTime;

    @Column(name = "campaign")
    private String            campaign;

    @Transient
    @JsonIgnore
    private Integer           expDays          = 7;

    public UrlShortner() {

    }

    public UrlShortner(
            String longUrl,
            String shortUrl,
            Timestamp createdAt,
            Timestamp expiry,
            int count,
            Timestamp lastHitTime,
            String campaign) {

        this.longUrl = longUrl;
        this.shortUrl = shortUrl;
        this.createdAt = createdAt;
        this.expiry = expiry;
        this.hitCount = count;
        this.lastHitTime = lastHitTime;
        this.campaign = campaign;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getExpiry() {
        return expiry;
    }

    public void setExpiry(Timestamp expiry) {
        this.expiry = expiry;
    }

    public int getHitCount() {
        return hitCount;
    }

    public void setHitCount(int count) {
        this.hitCount = count;
    }

    public Timestamp getLastHitTime() {
        return lastHitTime;
    }

    public void setLastHitTime(Timestamp lastHitTime) {
        this.lastHitTime = lastHitTime;
    }

    public Integer getExpDays() {
        return expDays;
    }

    public void setExpDays(Integer expDays) {
        this.expDays = expDays;
    }

    public String getCampaign() {
        return campaign;
    }

    public void setCampaign(String campaign) {
        this.campaign = campaign;
    }

}
