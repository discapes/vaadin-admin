package com.example.application.data.repository;

import com.example.application.data.entity.Contact;
import com.example.application.data.entity.Status;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    @Query("select c from Contact c " +
            "join fetch c.company " +
            "join fetch c.status " +
            "where (lower(c.firstName) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(c.lastName) like lower(concat('%', :searchTerm, '%'))" +
            "or lower(c.email) like lower(concat('%', :searchTerm, '%')))" +
            "and (c.status = :status or :status is null)")
    List<Contact> search(@Param("searchTerm") String searchTerm, @Param("status") Status status, Pageable pageable);
}
