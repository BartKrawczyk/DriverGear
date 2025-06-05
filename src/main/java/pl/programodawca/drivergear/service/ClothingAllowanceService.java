package pl.programodawca.drivergear.service;

import pl.programodawca.drivergear.dto.ClothingAllowanceDTO;
import pl.programodawca.drivergear.model.AllowanceStatus;

import java.time.LocalDate;
import java.util.List;

public interface ClothingAllowanceService {
    /**
     * Tworzy nowy przydział odzieżowy dla pracownika
     * @param employeeId ID pracownika
     * @param positionId ID stanowiska
     * @param startDate Data rozpoczęcia przydziału
     * @param endDate Data zakończenia przydziału
     * @param notes Dodatkowe uwagi
     * @return DTO z danymi utworzonego przydziału
     */
    ClothingAllowanceDTO createAllowance(Long employeeId, Long positionId, LocalDate startDate, LocalDate endDate, String notes);

    /**
     * Pobiera przydział odzieżowy po ID
     * @param allowanceId ID przydziału
     * @return DTO z danymi przydziału
     */
    ClothingAllowanceDTO getAllowanceById(Long allowanceId);

    /**
     * Pobiera wszystkie przydziały odzieżowe dla pracownika
     * @param employeeId ID pracownika
     * @return Lista DTO z danymi przydziałów
     */
    List<ClothingAllowanceDTO> getAllowancesByEmployeeId(Long employeeId);

    /**
     * Pobiera przydziały odzieżowe dla pracownika o określonym statusie
     * @param employeeId ID pracownika
     * @param status Status przydziału
     * @return Lista DTO z danymi przydziałów
     */
    List<ClothingAllowanceDTO> getAllowancesByEmployeeIdAndStatus(Long employeeId, AllowanceStatus status);

    /**
     * Pobiera przydziały odzieżowe dla stanowiska
     * @param positionId ID stanowiska
     * @return Lista DTO z danymi przydziałów
     */
    List<ClothingAllowanceDTO> getAllowancesByPositionId(Long positionId);

    /**
     * Pobiera przydziały odzieżowe o określonym statusie
     * @param status Status przydziału
     * @return Lista DTO z danymi przydziałów
     */
    List<ClothingAllowanceDTO> getAllowancesByStatus(AllowanceStatus status);

    /**
     * Pobiera przydziały odzieżowe aktywne w określonym dniu
     * @param date Data, dla której sprawdzamy aktywność przydziałów
     * @return Lista DTO z danymi przydziałów
     */
    List<ClothingAllowanceDTO> getActiveAllowancesOnDate(LocalDate date);

    /**
     * Pobiera przydziały odzieżowe, które wygasły, ale nadal mają status ACTIVE
     * @param date Data, względem której sprawdzamy wygaśnięcie
     * @return Lista DTO z danymi przydziałów
     */
    List<ClothingAllowanceDTO> getExpiredAllowances(LocalDate date);

    /**
     * Aktualizuje status przydziału odzieżowego
     * @param allowanceId ID przydziału
     * @param status Nowy status
     * @return DTO z zaktualizowanymi danymi przydziału
     */
    ClothingAllowanceDTO updateAllowanceStatus(Long allowanceId, AllowanceStatus status);

    /**
     * Aktualizuje daty przydziału odzieżowego
     * @param allowanceId ID przydziału
     * @param startDate Nowa data rozpoczęcia
     * @param endDate Nowa data zakończenia
     * @return DTO z zaktualizowanymi danymi przydziału
     */
    ClothingAllowanceDTO updateAllowanceDates(Long allowanceId, LocalDate startDate, LocalDate endDate);

    /**
     * Aktualizuje uwagi do przydziału odzieżowego
     * @param allowanceId ID przydziału
     * @param notes Nowe uwagi
     * @return DTO z zaktualizowanymi danymi przydziału
     */
    ClothingAllowanceDTO updateAllowanceNotes(Long allowanceId, String notes);

    /**
     * Anuluje przydział odzieżowy
     * @param allowanceId ID przydziału
     * @param reason Powód anulowania
     * @return DTO z zaktualizowanymi danymi przydziału
     */
    ClothingAllowanceDTO cancelAllowance(Long allowanceId, String reason);

    /**
     * Automatycznie aktualizuje statusy przydziałów odzieżowych
     * Zmienia status na EXPIRED dla przydziałów, które wygasły
     * @return Liczba zaktualizowanych przydziałów
     */
    int updateExpiredAllowances();
}
