package com.ll.exam.app10.app.member.service;

import com.ll.exam.app10.app.member.entity.MemberEntity;
import com.ll.exam.app10.app.member.repository.MemberRepository;
import com.ll.exam.app10.app.util.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {
    @Value("${custom.genFileDirPath}")
    private String genFileDirPath;

    private final MemberRepository memberRepository;

    public MemberEntity getMemberByUsername(String username) {
        return memberRepository.findByUsername(username).orElse(null);
    }

    public String getCurrentProfileImgDirName() {
        return "member/" + Util.date.getCurrentDateFormatted("yyyy_MM_dd");
    }

    public MemberEntity join(String username, String password, String email, MultipartFile img) {
        String profileImgRelPath = saveProfileImg(img);

        MemberEntity member = MemberEntity.builder()
                .username(username)
                .password(password)
                .email(email)
                .img(profileImgRelPath)
                .build();

        memberRepository.save(member);

        return member;
    }

    private String saveProfileImg(MultipartFile img) {
        if ( img == null || img.isEmpty() ) {
            return null;
        }

        String profileImgDirName = getCurrentProfileImgDirName();

        String ext = Util.file.getExt(img.getOriginalFilename());

        String fileName = UUID.randomUUID() + "." + ext;
        String profileImgDirPath = genFileDirPath + "/" + profileImgDirName;
        String profileImgFilePath = profileImgDirPath + "/" + fileName;

        new File(profileImgDirPath).mkdirs();

        try {
            img.transferTo(new File(profileImgFilePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return profileImgDirName + "/" + fileName;
    }

    public MemberEntity join(String username, String password, String email) {
        MemberEntity member = MemberEntity.builder()
                .username(username)
                .password(password)
                .email(email)
                .build();

        memberRepository.save(member);

        return member;
    }

    public MemberEntity getMemberById(Long id) {
        return memberRepository.findById(id).orElse(null);
    }


    public long count() {
        return memberRepository.count();
    }

    public void removeProfileImg(MemberEntity member) {
        member.removeProfileImgOnStorage();
        member.setImg(null);

        memberRepository.save(member);
    }

    public void setProfileImgByUrl(MemberEntity member, String url) {
        String filePath = Util.file.downloadImg(url, genFileDirPath + "/" + getCurrentProfileImgDirName() + "/" + UUID.randomUUID());
        member.setImg(getCurrentProfileImgDirName() + "/" + new File(filePath).getName());
        memberRepository.save(member);
    }

    public void modify(MemberEntity member, String email, MultipartFile profileImg) {
        removeProfileImg(member);
        String profileImgRelPath = saveProfileImg(profileImg);

        member.setEmail(email);
        member.setImg(profileImgRelPath);
        memberRepository.save(member);
    }
}
