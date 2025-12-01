package com.hbloc.youtube_tool_demo.plan.application;

import com.hbloc.youtube_tool_demo.common.modal.AppException;
import com.hbloc.youtube_tool_demo.feature.domain.FeatureEntity;
import com.hbloc.youtube_tool_demo.feature.infrastructure.FeatureRepository;
import com.hbloc.youtube_tool_demo.plan.api.mapper.PlanFeatureMapper;
import com.hbloc.youtube_tool_demo.plan.api.mapper.PlanMapper;
import com.hbloc.youtube_tool_demo.plan.api.modal.request.CreatePlanFeatureRequest;
import com.hbloc.youtube_tool_demo.plan.api.modal.request.CreatePlanRequest;
import com.hbloc.youtube_tool_demo.plan.api.modal.request.UpdatePlanFeatureRequest;
import com.hbloc.youtube_tool_demo.plan.api.modal.request.UpdatePlanRequest;
import com.hbloc.youtube_tool_demo.plan.domain.BillingPeriodEnum;
import com.hbloc.youtube_tool_demo.plan.domain.OveragePolicyEnum;
import com.hbloc.youtube_tool_demo.plan.domain.PlanEntity;
import com.hbloc.youtube_tool_demo.plan.domain.PlanFeatureEntity;
import com.hbloc.youtube_tool_demo.plan.infrastructure.PlanFeatureRepository;
import com.hbloc.youtube_tool_demo.plan.infrastructure.PlanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanServiceTest {

    @Mock
    private PlanRepository planRepository;

    @Mock
    private FeatureRepository featureRepository;

    @Mock
    private PlanFeatureRepository planFeatureRepository;

    @Mock
    private PlanFeatureMapper planFeatureMapper;

    @Mock
    private PlanMapper planMapper;

    @InjectMocks
    private PlanService planService;

    @Test
    void getPlans_returnsMappedPlans() {
        PlanEntity domainPlan = new PlanEntity();
        List<PlanEntity> domainPlans = List.of(domainPlan);
        List<com.hbloc.youtube_tool_demo.plan.api.modal.Plan> apiPlans = List.of(new com.hbloc.youtube_tool_demo.plan.api.modal.Plan());

        when(planRepository.findAllByActiveIsTrue()).thenReturn(Optional.of(domainPlans));
        when(planMapper.toApiList(domainPlans)).thenReturn(apiPlans);

        List<com.hbloc.youtube_tool_demo.plan.api.modal.Plan> result = planService.getPlans();

        assertEquals(apiPlans, result);
        verify(planRepository).findAllByActiveIsTrue();
        verify(planMapper).toApiList(domainPlans);
    }

    @Test
    void updatePlan_updatesFieldsAndFeatures() {
        PlanEntity planEntity = new PlanEntity();
        planEntity.setId(1);
        planEntity.setName("Old");
        planEntity.setPrice(new BigDecimal("5.00"));
        planEntity.setCurrency("USD");
        planEntity.setBillingPeriod(BillingPeriodEnum.MONTHLY);
        planEntity.setActive(true);

        FeatureEntity feature = new FeatureEntity();
        feature.setCode("UPLOADS");

        PlanFeatureEntity planFeature = new PlanFeatureEntity();
        planFeature.setPlanId(1);
        planFeature.setFeature(feature);
        planFeature.setIncludedUnits(100L);
        planFeature.setOveragePolicy(OveragePolicyEnum.CREDITS);

        List<PlanFeatureEntity> planFeatures = new ArrayList<>();
        planFeatures.add(planFeature);

        UpdatePlanFeatureRequest updatePlanFeatureRequest = new UpdatePlanFeatureRequest();
        updatePlanFeatureRequest.setIncludedUnit(200L);
        updatePlanFeatureRequest.setOveragePolicy("BLOCK");

        UpdatePlanRequest updatePlanRequest = new UpdatePlanRequest();
        updatePlanRequest.setName("New");
        updatePlanRequest.setPrice(new BigDecimal("9.99"));
        updatePlanRequest.setCurrency("VND");
        updatePlanRequest.setBillingPeriod("YEARLY");
        updatePlanRequest.setIsActive(false);
        updatePlanRequest.setFeatures(Map.of("UPLOADS", updatePlanFeatureRequest));

        when(planRepository.findByCode("BASIC")).thenReturn(Optional.of(planEntity));
        when(planFeatureRepository.findByPlanId(1)).thenReturn(Optional.of(planFeatures));

        planService.updatePlan("BASIC", updatePlanRequest);

        assertEquals("New", planEntity.getName());
        assertEquals(new BigDecimal("9.99"), planEntity.getPrice());
        assertEquals("VND", planEntity.getCurrency());
        assertEquals(BillingPeriodEnum.YEARLY, planEntity.getBillingPeriod());
        assertFalse(planEntity.getActive());

        assertEquals(200L, planFeature.getIncludedUnits());
        assertEquals(OveragePolicyEnum.BLOCK, planFeature.getOveragePolicy());
        verify(planRepository).save(planEntity);
        verify(planFeatureRepository).saveAll(planFeatures);
    }

    @Test
    void updatePlan_throwsWhenPlanMissing() {
        UpdatePlanRequest updatePlanRequest = new UpdatePlanRequest();
        updatePlanRequest.setFeatures(Collections.emptyMap());

        when(planRepository.findByCode("missing")).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> planService.updatePlan("missing", updatePlanRequest));
        verify(planFeatureRepository, never()).findByPlanId(any());
    }

    @Test
    void updatePlan_throwsWhenPlanFeaturesMissing() {
        PlanEntity planEntity = new PlanEntity();
        planEntity.setId(2);

        UpdatePlanRequest updatePlanRequest = new UpdatePlanRequest();
        updatePlanRequest.setName("Name");
        updatePlanRequest.setPrice(new BigDecimal("1.00"));
        updatePlanRequest.setCurrency("USD");
        updatePlanRequest.setBillingPeriod("MONTHLY");
        updatePlanRequest.setIsActive(true);
        updatePlanRequest.setFeatures(Collections.emptyMap());

        when(planRepository.findByCode("BASIC")).thenReturn(Optional.of(planEntity));
        when(planFeatureRepository.findByPlanId(2)).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> planService.updatePlan("BASIC", updatePlanRequest));
    }

    @Test
    void addPlan_savesPlanAndFeatures() {
        CreatePlanFeatureRequest featureRequest1 = new CreatePlanFeatureRequest();
        featureRequest1.setPlanCode("PRO");
        featureRequest1.setFeatureCode("UPLOADS");
        featureRequest1.setIncludedUnit(10L);

        CreatePlanFeatureRequest featureRequest2 = new CreatePlanFeatureRequest();
        featureRequest2.setPlanCode("PRO");
        featureRequest2.setFeatureCode("STORAGE");
        featureRequest2.setIncludedUnit(20L);

        CreatePlanRequest createPlanRequest = new CreatePlanRequest();
        createPlanRequest.setCode("PRO");
        createPlanRequest.setName("Pro");
        createPlanRequest.setPrice(new BigDecimal("10.00"));
        createPlanRequest.setCurrency("USD");
        createPlanRequest.setBillingPeriod("MONTHLY");
        createPlanRequest.setFeatures(List.of(featureRequest1, featureRequest2));

        PlanEntity planToSave = new PlanEntity();
        PlanEntity savedPlan = new PlanEntity();
        savedPlan.setId(9);

        FeatureEntity feature1 = new FeatureEntity();
        feature1.setId(1);
        feature1.setCode("UPLOADS");
        FeatureEntity feature2 = new FeatureEntity();
        feature2.setId(2);
        feature2.setCode("STORAGE");

        PlanFeatureEntity planFeature1 = new PlanFeatureEntity();
        planFeature1.setPlanId(savedPlan.getId());
        planFeature1.setFeatureId(feature1.getId());
        PlanFeatureEntity planFeature2 = new PlanFeatureEntity();
        planFeature2.setPlanId(savedPlan.getId());
        planFeature2.setFeatureId(feature2.getId());

        when(planRepository.findByCode("PRO")).thenReturn(Optional.empty());
        when(planMapper.toPlanEntity(createPlanRequest)).thenReturn(planToSave);
        when(planRepository.save(planToSave)).thenReturn(savedPlan);
        when(featureRepository.findByCode("UPLOADS")).thenReturn(Optional.of(feature1));
        when(featureRepository.findByCode("STORAGE")).thenReturn(Optional.of(feature2));
        when(planFeatureMapper.toPlanFeature(featureRequest1, savedPlan.getId(), feature1.getId())).thenReturn(planFeature1);
        when(planFeatureMapper.toPlanFeature(featureRequest2, savedPlan.getId(), feature2.getId())).thenReturn(planFeature2);

        planService.addPlan(createPlanRequest);

        verify(planRepository).save(planToSave);

        ArgumentCaptor<List<PlanFeatureEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(planFeatureRepository).saveAll(captor.capture());
        List<PlanFeatureEntity> savedPlanFeatures = captor.getValue();
        assertEquals(2, savedPlanFeatures.size());
        assertTrue(savedPlanFeatures.contains(planFeature1));
        assertTrue(savedPlanFeatures.contains(planFeature2));
    }

    @Test
    void addPlan_throwsWhenPlanCodeExists() {
        CreatePlanRequest createPlanRequest = new CreatePlanRequest();
        createPlanRequest.setCode("PRO");

        when(planRepository.findByCode("PRO")).thenReturn(Optional.of(new PlanEntity()));

        assertThrows(AppException.class, () -> planService.addPlan(createPlanRequest));
        verify(planRepository, never()).save(any());
        verify(planFeatureRepository, never()).saveAll(any());
    }

    @Test
    void deletePlan_marksPlanInactive() {
        PlanEntity plan = new PlanEntity();
        plan.setActive(true);

        when(planRepository.findByCode("PRO")).thenReturn(Optional.of(plan));

        planService.deletePlan("PRO");

        assertFalse(plan.getActive());
        verify(planRepository).save(plan);
    }

    @Test
    void deletePlan_throwsWhenPlanMissing() {
        when(planRepository.findByCode("PRO")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> planService.deletePlan("PRO"));
    }
}
