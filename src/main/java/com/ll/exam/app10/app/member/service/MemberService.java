package com.ll.exam.app10.app.member.service;

import com.ll.exam.app10.app.member.entity.MemberEntity;
import com.ll.exam.app10.app.member.repository.MemberRepository;
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
public class MemberService implements UserDetailsService {
    @Value("${custom.genFileDirPath}")
    private String genFileDirPath;

    private final MemberRepository memberRepository;

    public MemberEntity getMemberByUsername(String username) {
        return memberRepository.findByUsername(username).orElse(null);
    }

    public MemberEntity join(String username, String password, String email, MultipartFile img) {
        String profileImgRelPath = "member/" + UUID.randomUUID().toString() + ".png";
        File profileImgFile = new File(genFileDirPath + "/" + profileImgRelPath);

        profileImgFile.mkdirs(); // 관련된 폴더가 혹시나 없다면 만들어준다.

        try {
            img.transferTo(profileImgFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        MemberEntity member = MemberEntity.builder()
                .username(username)
                .password(password)
                .email(email)

                .img(profileImgRelPath)
                .build();

        memberRepository.save(member);

        return member;
    }

    public MemberEntity getMemberById(Long id) {
        return memberRepository.findById(id).orElse(null);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException { // spring security 사용 시 꼭 필요하며, user의 자세한 정보를 갖고있음.
        MemberEntity member = memberRepository.findByUsername(username).get();

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("member"));
        return new User(member.getUsername(), member.getPassword(), authorities);
    }
}
