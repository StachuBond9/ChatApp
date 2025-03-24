package com.staislawwojcik.forum.infrastructure.database.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, String> {
}
