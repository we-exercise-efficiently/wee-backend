package com.wee.demo.service;

import com.wee.demo.domain.User;
import com.wee.demo.domain.community.CrewCommunity;
import com.wee.demo.dto.request.CrewDto;
import com.wee.demo.repository.CrewRepository;
import com.wee.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CrewService {

    private final UserRepository userRepository;
    private final CrewRepository crewRepository;

    @Transactional
    public CrewDto write(Long userId, CrewDto crewDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException(("사용자가 존재하지 않습니다.")));

        CrewCommunity crewCommunity = CrewCommunity.builder()
                .user(user)
                .title(crewDto.getTitle())
                .content(crewDto.getContent())
                .likes(crewDto.getLikes())
                .createDate(crewDto.getCreateDate())
                .hit(crewDto.getHit())
                .commentCnt(crewDto.getCommentCnt())
                .image(crewDto.getImage())
                .period(crewDto.getPeriod())
                .location(crewDto.getLocation())
                .type(crewDto.getType())
                .headCount(crewDto.getHeadCount())
                .crewStatus(crewDto.getCrewStatus())
                .build();

        CrewCommunity savedOne = crewRepository.save(crewCommunity);
        crewDto.setCrewId(savedOne.getId());

        return crewDto;
    }

    @Transactional
    public CrewDto getCrew(Long crewId) {
        CrewCommunity crewCommunity = crewRepository.findById(crewId).orElseThrow(() -> new IllegalArgumentException(("게시물이 존재하지 않습니다.")));
        //Optional<CrewCommunity> crewCommunity = crewRepository.findByCommunityId(crewId);
        CrewDto crewDto = CrewDto.builder()
                .crewId(crewCommunity.getId())
                .title(crewCommunity.getTitle())
                .content(crewCommunity.getContent())
                .likes(crewCommunity.getLikes())
                .createDate(crewCommunity.getCreateDate())
                .hit(crewCommunity.getHit())
                .commentCnt(crewCommunity.getCommentCnt())
                .image(crewCommunity.getImage())
                .period(crewCommunity.getPeriod())
                .location(crewCommunity.getLocation())
                .type(crewCommunity.getType())
                .headCount(crewCommunity.getHeadCount())
                .crewStatus(crewCommunity.getCrewStatus())
                .build();

        return crewDto;
    }

    @Transactional
    public CrewDto update(Long crewId, CrewDto crewDto) {
        CrewCommunity crewCommunity = crewRepository.findById(crewId).orElseThrow(() -> new IllegalArgumentException(("게시물이 존재하지 않습니다.")));
        crewCommunity.setTitle(crewDto.getTitle());
        crewCommunity.setContent(crewDto.getContent());
        crewCommunity.setCreateDate(crewDto.getCreateDate());
        crewCommunity.setImage(crewDto.getImage());
        crewCommunity.setPeriod(crewDto.getPeriod());
        crewCommunity.setLocation(crewDto.getLocation());
        crewCommunity.setType(crewDto.getType());
        crewCommunity.setHeadCount(crewDto.getHeadCount());
        CrewCommunity updatedOne = crewRepository.save(crewCommunity);
        return crewDto;
    }

    @Transactional
    public void delete(Long crewId) {
        CrewCommunity crewCommunity = crewRepository.findById(crewId).orElseThrow(() -> new IllegalArgumentException(("게시물이 존재하지 않습니다.")));
        crewRepository.deleteById(crewId);
    }
}
