package com.example.social_network_api.repository;

import com.example.social_network_api.entity.Follow;
import com.example.social_network_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    Boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);
    Boolean existsByFollowerIdAndFollowingIdAndFollowStatus(Long followerId, Long followingId,  Follow.FollowStatus followStatus);


    @Query("select count(f) from Follow f where (f.follower.id = :userId or f.following.id = :userId) " +
            "and f.followStatus =: followStatus")
    Long countFriends(@Param("userId") Long userId, @Param("followStatus") Follow.FollowStatus followStatus);


    @Query("select case when f.follower.id = :userId then f.following else f.follower end " +
            "from Follow f " +
            "where (f.follower.id = :userId or f.following.id = :userId) " +
            "and f.followStatus = :followStatus")
    Set<User> findAllFriends(@Param("userId") Long userId, @Param("followStatus") Follow.FollowStatus followStatus);


    //lấy ds người fl mình -> lấy all bản ghi với điều kiện người nhận là id của mình
    @Query("select f.follower from Follow f where f.following.id = :userId and f.followStatus in (:statuses)")
    Set<User> findFollowers(@Param("userId") Long userId, @Param("statuses") Set<Follow.FollowStatus> statuses);
    //lấy ds mình đang fl -> lấy all bản ghi với điều kiện người gởi là id của mình
    @Query("select f.following from Follow f where f.follower.id = :userId and f.followStatus in (:statuses)")
    Set<User> getFollowings(@Param("userId") Long userId, @Param("statuses") Set<Follow.FollowStatus> statuses);


    //lấy all lời mời mình đã gởi đi -> lấy ds folowings
    @Query("select f.following from Follow f where f.follower.id = :userId and f.followStatus = :followStatus")
    Set<User> findPendingRequestsSent(@Param("userId") Long userId, @Param("followStatus") Follow.FollowStatus followStatus);
    //lấy all lời mời mình đã nhận -> lấy ds followers
    @Query("select f.follower from Follow f where f.following.id = :userId and f.followStatus = :followStatus")
    Set<User> findPendingRequestsReceived(@Param("userId") Long userId, @Param("followStatus") Follow.FollowStatus followStatus);

}
