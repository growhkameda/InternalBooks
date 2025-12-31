package com.example.internalbooks.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.example.internalbooks.common.Const;

import lombok.Data;

@Data
@Entity
@Table(name = "m_department")
/**
 * MDepartmentテーブルからデータを受け取るためのEntity
 */
public class MDepartmentEntity {
    @Id
    @Column(name = Const.ID)
    private Integer id;

    @Column(name = Const.NAME)
    private String name;

    @Column(name = Const.DETAIL)
    private String detail;


    }
