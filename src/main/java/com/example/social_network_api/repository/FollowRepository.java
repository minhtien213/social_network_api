package com.example.social_network_api.repository;

import com.example.social_network_api.entity.Follow;
import com.example.social_network_api.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    Boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

    Boolean existsByFollowerIdAndFollowingIdAndFollowStatus(Long followerId, Long followingId, Follow.FollowStatus followStatus);

    @Query("select (count(f) > 0) from Follow f where " +
            "((f.follower.id = :followerId and  f.following.id = :followingId) or " +
            "(f.follower.id = :followingId and  f.following.id = :followerId)) and " +
            "f.followStatus = :followStatus")
    Boolean isFriend(@Param("followerId") Long followerId, @Param("followingId") Long followingId, Follow.FollowStatus followStatus);

    @Query("select f from Follow f where " +
            "(f.follower.id = :followerId and  f.following.id = :followingId) or " +
            "(f.follower.id = :followingId and  f.following.id = :followerId)")
    Follow existsFollow(@Param("followerId") Long followerId, @Param("followingId") Long followingId);

    @Query("select count(f) from Follow f where (f.follower.id = :userId or f.following.id = :userId) and " +
            "f.followStatus = :followStatus")
    Long countFriends(@Param("userId") Long userId, @Param("followStatus") Follow.FollowStatus followStatus);

    // CASE WHEN : trả về ds id không trả về entity -> lấy ds entity từ ds id
    @Query("select u from User u " +
            "where u.id in (select case when f.follower.id = :userId then f.following.id else f.follower.id end " +
            "from Follow f where (f.follower.id = :userId or f.following.id = :userId) " +
            "and f.followStatus = :followStatus)")
    Page<User> findAllFriends(@Param("userId") Long userId, @Param("followStatus") Follow.FollowStatus followStatus, Pageable pageable);


    //lấy ds người fl mình -> lấy all bản ghi với điều kiện người nhận là id của mình
    @Query("select u from User u " +
            "where u.id in (select case when f.follower.id = :userId then f.following.id else f.follower.id end " +
            "from Follow f where ((f.follower.id = :userId or f.following.id = :userId) and f.followStatus = :accepted) " +
            "or (f.following.id = :userId and f.followStatus = :pending))")
    Set<User> findFollowers(@Param("userId") Long userId, @Param("accepted") Follow.FollowStatus accepted,
                            @Param("pending") Follow.FollowStatus pending);

    //lấy ds mình đang fl -> lấy all bản ghi với điều kiện người gởi là id của mình
    @Query("select u from User u " +
            "where u.id in (select case when f.follower.id = :userId then f.following.id else f.follower.id end " +
            "from Follow f where ((f.follower.id = :userId or f.following.id = :userId) and f.followStatus = :accepted) " +
            "or (f.follower.id = :userId and f.followStatus = :pending))")
    Set<User> getFollowings(@Param("userId") Long userId, @Param("accepted") Follow.FollowStatus accepted,
                            @Param("pending") Follow.FollowStatus pending);

    //lấy all lời mời mình đã gởi đi -> lấy ds folowings
    @Query("select f.following from Follow f where f.follower.id = :userId and f.followStatus = :followStatus")
    Set<User> findPendingRequestsSent(@Param("userId") Long userId, @Param("followStatus") Follow.FollowStatus followStatus);

    //lấy all lời mời mình đã nhận -> lấy ds followers
    @Query("select f.follower from Follow f where f.following.id = :userId and f.followStatus = :followStatus")
    Set<User> findPendingRequestsReceived(@Param("userId") Long userId, @Param("followStatus") Follow.FollowStatus followStatus);

}
