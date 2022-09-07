package com.ll.exam.app10.app.member.entity;

import com.ll.exam.app10.app.base.entity.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class MemberEntity extends BaseEntity {
    @Column(unique = true)
    private String username;
    private String password;
    private String email;
    private String img;
}

//@Entity
//@Setter
//@Getter
//@AllArgsConstructor
//@NoArgsConstructor
//@SuperBuilder
//@ToString(callSuper = true)
//public class MemberEntity extends BaseEntity {
//    private String relTypeCode;
//    private int relId;
//    private String typeCode;
//    private String type2Code;
//    private String fileExtTypeCode;
//    private String fileExtType2Code;
//    private int fileSize;
//    private int fileNo;
//    private String fileExt;
//    private String fileDir;
//    private String originFileName;
//}
