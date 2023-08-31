package com.mungnyang.controller.product;

import com.mungnyang.dto.ErrorMessage;
import com.mungnyang.dto.product.accommodation.AccommodationFacilityDto;
import com.mungnyang.dto.product.accommodation.CreateAccommodationDto;
import com.mungnyang.dto.product.accommodation.room.CreateRoomDto;
import com.mungnyang.service.product.accommodation.AccommodationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/seller")
@RequiredArgsConstructor
public class AccommodationController {

    private final AccommodationService accommodationService;

    @GetMapping("/accommodation")
    public String loadRegisterPage(Model model) {
        accommodationService.initializeStore(model);
        return "seller/register";
    }

    @PostMapping("/accommodation")
    public ResponseEntity<?> registerAccommodation(CreateAccommodationDto createAccommodationDto,
                                                   @RequestParam("imageFile")
                                                   List<MultipartFile> accommodationImageFileList,
                                                   @RequestParam("facility")
                                                   List<AccommodationFacilityDto> accommodationFacilityList,
                                                   @RequestParam("room")
                                                   List<CreateRoomDto> roomList) {
        try {
            accommodationService.registerAccommodation(createAccommodationDto, accommodationImageFileList, accommodationFacilityList, roomList);
        } catch (Exception e) {
            return new ResponseEntity<ErrorMessage>(new ErrorMessage(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<String>("success", HttpStatus.OK);
    }
}
