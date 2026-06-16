package com.AuraMoon.auramoon.billing.service;

import com.AuraMoon.auramoon.billing.dto.NightAuditResultDTO;

public interface IFolioConsolidationService {
    NightAuditResultDTO consolidateAllActiveFolios(Integer actorId);
}
