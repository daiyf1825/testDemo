package com.jy;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.ixiye.boot.rpc.dubbo.annotation.RpcConsumer;
import com.ixiye.common.exception.ServiceException;
import com.ixiye.common.model.Response;
import com.ixiye.sanya.ucenter.dto.UserDto;
import com.ixiye.sanya.ucenter.model.AppUserInfo;
import com.ixiye.sanya.ucenter.model.User;
import com.ixiye.sanya.ucenter.model.UserProfile;
import com.ixiye.sanya.ucenter.service.AppUserInfoReadService;
import com.ixiye.sanya.ucenter.service.UserProfileReadService;
import com.ixiye.sanya.ucenter.service.UserReadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * Desc: 用户缓存
 * Date: 2017/5/18
 */
@Slf4j
@Component
public class UserCache {

    private LoadingCache<Long, User> userLoadingCache;
    private LoadingCache<Long, UserProfile> userProfileLoadingCache;
    private LoadingCache<Long, AppUserInfo> appUserInfoLoadingCache;

    @RpcConsumer
    private UserReadService userReadService;
    @RpcConsumer
    private UserProfileReadService userProfileReadService;
    @RpcConsumer
    private AppUserInfoReadService appUserInfoReadService;

    @Value("${cache.duration.in.minutes: 30}")
    private Integer duration;

    @PostConstruct
    public void init() {
        this.userLoadingCache = CacheBuilder.newBuilder()
                .expireAfterWrite(duration, TimeUnit.MINUTES)
                .maximumSize(10000)
                .build(new CacheLoader<Long, User>() {
                    @Override
                    public User load(Long uid) {
                        Response<User> resp = userReadService.findById(uid);
                        if (!resp.isSuccess()) {
                            log.error("failed to find user by id:{}, cause:{}", uid, resp.getError());
                            throw new ServiceException(resp.getError());
                        }
                        return resp.getResult();
                    }
                });

        this.userProfileLoadingCache = CacheBuilder.newBuilder()
                .expireAfterWrite(duration, TimeUnit.MINUTES)
                .maximumSize(10000)
                .build(new CacheLoader<Long, UserProfile>() {
                    @Override
                    public UserProfile load(Long uid) {
                        Response<UserProfile> resp = userProfileReadService.findByUid(uid);
                        if (!resp.isSuccess()) {
                            log.error("failed to find user by id:{}, cause:{}", uid, resp.getError());
                            throw new ServiceException(resp.getError());
                        }
                        return resp.getResult();
                    }
                });

        this.appUserInfoLoadingCache = CacheBuilder.newBuilder()
                .expireAfterWrite(duration, TimeUnit.MINUTES)
                .maximumSize(10000)
                .build(new CacheLoader<Long, AppUserInfo>() {
                    @Override
                    public AppUserInfo load(Long uid) {
                        Response<AppUserInfo> resp = appUserInfoReadService.findByUid(uid);
                        if (!resp.isSuccess()) {
                            log.error("failed to find user by id:{}, cause:{}", uid, resp.getError());
                            throw new ServiceException(resp.getError());
                        }
                        return resp.getResult();
                    }
                });
    }

    /**
     * 查询uid查询用户
     *
     * @param uid
     * @return
     */
    public User findUserByUid(Long uid) {
        return userLoadingCache.getUnchecked(uid);
    }

    /**
     * 根据uid查询用户信息
     *
     * @param uid
     * @return
     */
    public AppUserInfo findAppUserInfo(Long uid) {
        return appUserInfoLoadingCache.getUnchecked(uid);
    }

    /**
     * 根据uid查询用户信息
     *
     * @param uid
     * @return
     */
    public UserDto findUserDtoByUid(Long uid) {
        User user = userLoadingCache.getUnchecked(uid);
        UserProfile userProfile = userProfileLoadingCache.getUnchecked(uid);
        AppUserInfo appUserInfo = appUserInfoLoadingCache.getUnchecked(uid);
        return new UserDto(user, userProfile, appUserInfo);
    }

}
