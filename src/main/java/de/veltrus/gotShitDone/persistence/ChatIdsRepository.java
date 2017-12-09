package de.veltrus.gotShitDone.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

@Transactional
public interface ChatIdsRepository extends JpaRepository<ChatIdContact, Long> {

}
