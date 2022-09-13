package com.ll.exam.app10.app.member.entity;

import com.ll.exam.app10.app.base.AppConfig;
import com.ll.exam.app10.app.base.entity.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.File;

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

        public void removeProfileImgOnStorage() {
            if(img == null || img.trim().length() == 0) return;

            String profileImgPath = getImgPath();

            new File(profileImgPath).delete();
        }

        private String getImgPath() {
            return AppConfig.GET_FILE_DIR_PATH + "/" + img;
        }

        public String getProfileImgUrl() {
        if ( img == null ) return null;

        return "/gen/" + img;
    }
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
