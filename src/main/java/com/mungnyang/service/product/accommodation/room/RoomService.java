package com.mungnyang.service.product.accommodation.room;

import com.mungnyang.constant.Booked;
import com.mungnyang.constant.Status;
import com.mungnyang.dto.product.accommodation.FacilityDto;
import com.mungnyang.dto.product.accommodation.room.CreateRoomDto;
import com.mungnyang.entity.product.accommodation.Accommodation;
import com.mungnyang.entity.product.accommodation.room.Room;
import com.mungnyang.repository.product.accommodation.room.RoomRepository;
import com.mungnyang.service.product.StatusService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper;
    private final RoomImageService roomImageService;
    private final RoomFacilityService roomFacilityService;

    /**
     * Room, RoomFacility, RoomImage 신규 저장
     * @param accommodation Room 참조할 Accommodation
     * @param roomList 페이지에 입력된 Room 정보 리스트
     */
    public void saveRoom(Accommodation accommodation, List<CreateRoomDto> roomList) throws Exception {
        for (CreateRoomDto createRoomDto : roomList) {
            Room createdRoom = createdRoom(accommodation, createRoomDto);
            roomRepository.save(createdRoom);
            List<MultipartFile> roomImageFileList = createRoomDto.getImageFile();
            roomImageService.saveRoomImages(createdRoom, roomImageFileList);
            List<FacilityDto> roomFacilityList = createRoomDto.getFacilityList();
            roomFacilityService.saveRoomFacility(createdRoom, roomFacilityList);
        }
    }

    /**
     * 신규 Room 객체 생성
     * @param accommodation Room 참조할 Accommodation
     * @param createRoomDto 페이지에 입력된 Room 정보
     * @return 생성된 Room 객체
     */
    private Room createdRoom(Accommodation accommodation, CreateRoomDto createRoomDto) {
        modelMapper.typeMap(CreateRoomDto.class, Room.class).addMappings(mapping -> {
            mapping.using((Converter<String, Status>) ctx -> StatusService.statusConverter(ctx.getSource()))
                    .map(CreateRoomDto::getRoomStatus, Room::setRoomStatus);
            mapping.using((Converter<String, Booked>) ctx -> StatusService.isAvailableConverter(ctx.getSource()))
                    .map(CreateRoomDto::getIsAvailable, Room::setIsAvailable);
            mapping.skip(Room::setRoomId);
            mapping.skip(Room::setAccommodation);
        });
        Room createdRoom = modelMapper.map(createRoomDto, Room.class);
        createdRoom.setAccommodation(accommodation);
        return createdRoom;
    }

    /**
     * Accommodation 객체로 Room 객체 리스트 반환
     * @param accommodation 찾을 Accommodation 객체
     * @return 찾은 Room 리스트 없으면 예외 발생
     */
    public List<Room> findRoomByAccommodation(Accommodation accommodation) {
        List<Room> rooms = roomRepository.findByAccommodationAccommodationIdOrderByRoomId(accommodation.getAccommodationId());
        if (rooms.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return rooms;
    }
}
