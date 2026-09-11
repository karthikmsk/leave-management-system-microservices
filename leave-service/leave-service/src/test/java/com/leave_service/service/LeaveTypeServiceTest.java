package com.leave_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.leave_service.dto.LeaveTypeRequestDto;
import com.leave_service.dto.LeaveTypeResponseDto;
import com.leave_service.exception.DuplicateLeaveTypeException;
import com.leave_service.exception.LeaveTypeNotFoundException;
import com.leave_service.mapper.LeaveTypeMapper;
import com.leave_service.model.LeaveType;
import com.leave_service.repository.LeaveTypeRepository;

@ExtendWith(MockitoExtension.class)
public class LeaveTypeServiceTest {

    @Mock
    private LeaveTypeRepository leaveTypeRepository;

    @Mock
    private LeaveTypeMapper leaveTypeMapper;

    @InjectMocks
    private LeaveTypeService leaveTypeService;

    private LeaveType createLeaveType() {
        LeaveType leaveType = new LeaveType();
        leaveType.setId(1L);
        leaveType.setName("Annual Leave");
        leaveType.setDescription("Annual Leave");
        leaveType.setActive(true);
        leaveType.setAnnualAllocation(20F);
        leaveType.setCarryForwardAllowed(true);
        leaveType.setMaxCarryForwardDays(10F);
        return leaveType;
    }

    private LeaveTypeRequestDto createRequestDto() {
        LeaveTypeRequestDto dto = new LeaveTypeRequestDto();
        dto.setName("Annual Leave");
        dto.setDescription("Annual Leave");
        dto.setAnnualAllocation(20F);
        dto.setCarryForwardAllowed(true);
        dto.setMaxCarryForwardDays(10F);
        return dto;
    }

    private LeaveTypeResponseDto createResponseDto() {
        LeaveTypeResponseDto dto = new LeaveTypeResponseDto();
        dto.setId(1L);
        dto.setName("Annual Leave");
        dto.setDescription("Annual Leave");
        dto.setAnnualAllocation(20F);
        dto.setCarryForwardAllowed(true);
        dto.setMaxCarryForwardDays(10F);
        dto.setActive(true);
        return dto;
    }

    @Test
    void getLeaveTypeById_ShouldReturnLeaveType() {
        LeaveType leaveType = createLeaveType();
        LeaveTypeResponseDto responseDto = createResponseDto();

        when(leaveTypeRepository.findById(1L)).thenReturn(Optional.of(leaveType));
        when(leaveTypeMapper.toResponseDto(leaveType)).thenReturn(responseDto);
        LeaveTypeResponseDto result = leaveTypeService.getLeaveTypeById(1L);

        assertNotNull(result);
        assertEquals("Annual Leave", result.getName());
        assertEquals("Annual Leave", result.getDescription());
        assertEquals(20F, result.getAnnualAllocation());
        assertTrue(result.getCarryForwardAllowed());
        assertEquals(10F, result.getMaxCarryForwardDays());
        assertTrue(result.isActive());
        verify(leaveTypeRepository, times(1)).findById(1L);
        verify(leaveTypeMapper, times(1)).toResponseDto(leaveType);
    }

    @Test
    void getLeaveTypeById_ShouldThrowLeaveTypeNotFoundException_WhenIdDoesNotExist() {
        when(leaveTypeRepository.findById(1L)).thenReturn(Optional.empty());

        LeaveTypeNotFoundException exception = assertThrows(LeaveTypeNotFoundException.class,
                () -> leaveTypeService.getLeaveTypeById(1L));
        assertEquals("Leave type not found", exception.getMessage());
        verify(leaveTypeRepository, times(1)).findById(1L);
        verifyNoInteractions(leaveTypeMapper);
    }

    @Test
    void createLeaveType_ShouldCreateLeaveType() {
        LeaveTypeRequestDto requestDto = createRequestDto();
        LeaveType leaveType = createLeaveType();
        LeaveTypeResponseDto responseDto = createResponseDto();

        when(leaveTypeRepository.existsByName(requestDto.getName())).thenReturn(false);
        when(leaveTypeMapper.toEntity(requestDto)).thenReturn(leaveType);
        when(leaveTypeRepository.save(leaveType)).thenReturn(leaveType);
        when(leaveTypeMapper.toResponseDto(leaveType)).thenReturn(responseDto);
        LeaveTypeResponseDto result = leaveTypeService.createLeaveType(requestDto);
        assertNotNull(result);
        assertEquals("Annual Leave", result.getName());
        assertEquals("Annual Leave", result.getDescription());
        assertEquals(20F, result.getAnnualAllocation());
        assertTrue(result.getCarryForwardAllowed());
        assertEquals(10F, result.getMaxCarryForwardDays());
        assertTrue(result.isActive());

        verify(leaveTypeRepository).existsByName(requestDto.getName());
        verify(leaveTypeMapper).toEntity(requestDto);
        verify(leaveTypeRepository).save(leaveType);
        verify(leaveTypeMapper).toResponseDto(leaveType);

    }

    @Test
    void createLeaveType_ShouldThrowDuplicateLeaveTypeException_WhenLeaveTypeAlreadyExists() {
        when(leaveTypeRepository.existsByName("Annual Leave")).thenReturn(true);
        LeaveTypeRequestDto requestDto = createRequestDto();
        DuplicateLeaveTypeException exception = assertThrows(DuplicateLeaveTypeException.class,
                () -> leaveTypeService.createLeaveType(requestDto));
        assertEquals("Leave type already exists", exception.getMessage());
        verify(leaveTypeRepository, times(1)).existsByName(requestDto.getName());
        verifyNoInteractions(leaveTypeMapper);

    }

    @Test
    void updateLeaveType_ShouldUpdateLeaveType() {
        LeaveTypeRequestDto requestDto = createRequestDto();
        LeaveType leaveType = createLeaveType();
        LeaveTypeResponseDto responseDto = createResponseDto();

        when(leaveTypeRepository.findById(1L)).thenReturn(Optional.of(leaveType));
        when(leaveTypeRepository.save(leaveType)).thenReturn(leaveType);
        when(leaveTypeMapper.toResponseDto(leaveType)).thenReturn(responseDto);

        LeaveTypeResponseDto result = leaveTypeService.updateLeaveType(1L, requestDto);
        assertNotNull(result);
        assertEquals("Annual Leave", result.getName());

        assertEquals(requestDto.getName(), leaveType.getName());
        assertEquals(requestDto.getDescription(), leaveType.getDescription());
        assertEquals(requestDto.getAnnualAllocation(), leaveType.getAnnualAllocation());

        verify(leaveTypeRepository).findById(1L);
        verify(leaveTypeRepository).save(leaveType);
        verify(leaveTypeMapper).toResponseDto(leaveType);

    }

    @Test
    void updateLeaveType_ShouldThrowLeaveTypeNotFoundException_WhenIdDoesNotExist() {
        LeaveTypeRequestDto request = createRequestDto();
        when(leaveTypeRepository.findById(1L)).thenReturn(Optional.empty());

        LeaveTypeNotFoundException exception = assertThrows(LeaveTypeNotFoundException.class,
                () -> leaveTypeService.updateLeaveType(1L,request));
        assertEquals("Leave type not found", exception.getMessage());
        verify(leaveTypeRepository, times(1)).findById(1L);
        verifyNoInteractions(leaveTypeMapper);
    }

    @Test
    void updateLeaveType_ShouldThrowException_WhenCarryForwardDaysIsNegative() {
        LeaveType leaveType = createLeaveType();
        LeaveTypeRequestDto request = createRequestDto();

        request.setCarryForwardAllowed(true);
        request.setMaxCarryForwardDays(-5F);

        when(leaveTypeRepository.findById(1L)).thenReturn(Optional.of(leaveType));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            ()-> leaveTypeService.updateLeaveType(1L, request));

        assertEquals("Max carry forward days cannot be negative.",exception.getMessage());
        verify(leaveTypeRepository).findById(1L);    
        verify(leaveTypeRepository, times(0)).save(leaveType);
        verifyNoInteractions(leaveTypeMapper);    
    }

    @Test
    void updateLeaveType_ShouldSetMaxCarryFowrdDaysZero_WhenCarryForwardNotAllowed(){
        LeaveType leaveType = createLeaveType();
        LeaveTypeRequestDto request = createRequestDto();

        request.setCarryForwardAllowed(false);
        request.setMaxCarryForwardDays(10F);

        LeaveTypeResponseDto response  = createResponseDto();
        response.setCarryForwardAllowed(false);
        response.setMaxCarryForwardDays(0F);

        when(leaveTypeRepository.findById(1L)).thenReturn(Optional.of(leaveType));
        when(leaveTypeRepository.save(leaveType)).thenReturn(leaveType);
        when(leaveTypeMapper.toResponseDto(leaveType)).thenReturn(response);

        LeaveTypeResponseDto result = leaveTypeService.updateLeaveType(1L,request);

        assertNotNull(result);
        assertFalse(result.getCarryForwardAllowed());
        assertEquals(0F, leaveType.getMaxCarryForwardDays());
        assertEquals(0F, result.getMaxCarryForwardDays());

        verify(leaveTypeRepository).findById(1L);    
        verify(leaveTypeRepository).save(leaveType);
        verify(leaveTypeMapper).toResponseDto(leaveType); 

    }

    @Test
    void activateLeaveType_ShouldActivateLeaveType() {
        LeaveType leaveType = createLeaveType();
        leaveType.setActive(false);

        LeaveTypeResponseDto responseDto = createResponseDto();
        responseDto.setActive(true);

        when(leaveTypeRepository.findById(1L)).thenReturn(Optional.of(leaveType));
        when(leaveTypeRepository.save(leaveType)).thenReturn(leaveType);
        when(leaveTypeMapper.toResponseDto(leaveType)).thenReturn(responseDto);

        LeaveTypeResponseDto result = leaveTypeService.activateLeaveType(1L);

        assertNotNull(result);
        assertTrue(result.isActive());
        assertTrue(leaveType.isActive());

        verify(leaveTypeRepository).findById(1L);
        verify(leaveTypeRepository).save(leaveType);
        verify(leaveTypeMapper).toResponseDto(leaveType);

    }

    @Test
    void activateLeaveType_ShouldThrowLeaveTypeNotFoundException_WhenIdDoesNotExist() {
        when(leaveTypeRepository.findById(1L)).thenReturn(Optional.empty());

        LeaveTypeNotFoundException exception = assertThrows(LeaveTypeNotFoundException.class,
                () -> leaveTypeService.activateLeaveType(1L));
        assertEquals("Leave type not found", exception.getMessage());
        verify(leaveTypeRepository).findById(1L);
        verifyNoInteractions(leaveTypeMapper);
    }

    @Test
    void activateLeaveType_ShouldDeactivateLeaveType() {
        LeaveType leaveType = createLeaveType();
        leaveType.setActive(true);

        LeaveTypeResponseDto responseDto = createResponseDto();
        responseDto.setActive(false);

        when(leaveTypeRepository.findById(1L)).thenReturn(Optional.of(leaveType));
        when(leaveTypeRepository.save(leaveType)).thenReturn(leaveType);
        when(leaveTypeMapper.toResponseDto(leaveType)).thenReturn(responseDto);

        LeaveTypeResponseDto result = leaveTypeService.deActivateLeaveType(1L);

        assertNotNull(result);
        assertFalse(result.isActive());
        assertFalse(leaveType.isActive());

        verify(leaveTypeRepository).findById(1L);
        verify(leaveTypeRepository).save(leaveType);
        verify(leaveTypeMapper).toResponseDto(leaveType);

    }

    @Test
    void deActivateLeaveType_ShouldThrowLeaveTypeNotFoundException_WhenIdDoesNotExist() {
        when(leaveTypeRepository.findById(1L)).thenReturn(Optional.empty());

        LeaveTypeNotFoundException exception = assertThrows(LeaveTypeNotFoundException.class,
                () -> leaveTypeService.deActivateLeaveType(1L));
        assertEquals("Leave type not found", exception.getMessage());
        verify(leaveTypeRepository).findById(1L);
        verifyNoInteractions(leaveTypeMapper);
    }

}
