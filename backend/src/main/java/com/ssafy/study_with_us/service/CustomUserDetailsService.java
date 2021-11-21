package com.ssafy.study_with_us.service;

import com.ssafy.study_with_us.domain.entity.Member;
import com.ssafy.study_with_us.domain.repository.MemberRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {
   private final MemberRepository memberRepository;

   public CustomUserDetailsService(MemberRepository memberRepository) {
      this.memberRepository = memberRepository;
   }

   @Override
   @Transactional
   public UserDetails loadUserByUsername(final String email) {
      User user1 = memberRepository.findOneWithAuthoritiesByEmail(email)
              .map(user -> createUser(email, user))
              .orElseThrow(() -> new UsernameNotFoundException(email + " -> 데이터베이스에서 찾을 수 없습니다."));
      return user1;
   }

   private User createUser(String email, Member user) {
      List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
              .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
              .collect(Collectors.toList());
      return new User(user.getEmail(),
              user.getPassword(),
              grantedAuthorities);
   }
}
