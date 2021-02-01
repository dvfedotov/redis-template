package com.redistemplate.repository;

import com.redistemplate.model.TemplateEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateEntityRepository extends CrudRepository<TemplateEntity, String> {
}
