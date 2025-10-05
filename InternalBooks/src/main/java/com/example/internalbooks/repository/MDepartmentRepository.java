package com.example.internalbooks.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.internalbooks.entity.MDepartmentEntity;


/**
 * m_departmentからidを指定して部署名を取得するリポジトリ
 */
public interface MDepartmentRepository extends JpaRepository<MDepartmentEntity, Integer> {
	
	// 指定された部署IDから部署名を取得
	@Query("SELECT d.name FROM MDepartmentEntity d WHERE d.id = :departmentId")
	Optional<String> findNameById(@Param("departmentId") Integer departmentId);
		
}
