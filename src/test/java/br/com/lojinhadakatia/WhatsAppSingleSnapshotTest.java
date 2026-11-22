package br.com.lojinhadakatia;

import br.com.lojinhadakatia.catalog.dto.ProductResponse;
import br.com.lojinhadakatia.catalog.dto.ProductPriceStatus;
import br.com.lojinhadakatia.catalog.service.CatalogService;
import br.com.lojinhadakatia.offering.dto.OfferingResponse;
import br.com.lojinhadakatia.offering.entity.OfferingType;
import br.com.lojinhadakatia.offering.service.OfferingService;
import br.com.lojinhadakatia.store.entity.StoreInformation;
import br.com.lojinhadakatia.store.repository.StoreInformationRepository;
import br.com.lojinhadakatia.whatsapp.dto.WhatsAppConfirmationRequest;
import br.com.lojinhadakatia.whatsapp.service.WhatsAppService;
import br.com.lojinhadakatia.shared.exception.BusinessException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** Validated prices and availability must not be re-read after confirmation. */
class WhatsAppSingleSnapshotTest {

    private record Fixture(WhatsAppService service, StoreInformationRepository store,
                           CatalogService catalog, OfferingService offerings) {}

    private Fixture fixture() {
        var store = mock(StoreInformationRepository.class);
        var catalog = mock(CatalogService.class);
        var offerings = mock(OfferingService.class);
        when(store.findById(1L)).thenReturn(Optional.of(
            new StoreInformation("Lojinha da Kátia", null, null, "5547999999999", null, null)));
        return new Fixture(new WhatsAppService(store,catalog,offerings),store,catalog,offerings);
    }

    @Test
    void productConfirmationUsesOneValidatedSnapshot() {
        var f = fixture();
        var p = new ProductResponse(12L,"Kit exemplo",null,new BigDecimal("120.00"),
            true,true,null,List.of(),List.of(),new BigDecimal("99.90"),
            ProductPriceStatus.CONFIRMED,2,null);
        when(f.catalog.published(12L)).thenReturn(p);
        var response = f.service.confirm(new WhatsAppConfirmationRequest(
            WhatsAppConfirmationRequest.Type.PRODUCT,12L,new BigDecimal("99.90"),Map.of()));
        assertTrue(response.url().startsWith("https://wa.me/5547999999999?text="));
        assertTrue(response.message().contains("Kit exemplo"));
        assertTrue(response.message().contains("99,90"));
        verify(f.catalog,times(1)).published(12L);
        verify(f.store,times(1)).findById(1L);
    }

    @Test
    void offeringWithoutCustomizationsAlsoUsesOneValidatedSnapshot() {
        var f = fixture();
        var item = new OfferingResponse(9L,"Oficina exemplo",null,
            OfferingType.WORKSHOP,new BigDecimal("49.90"),null,true,true,List.of());
        when(f.offerings.publicOne(9L)).thenReturn(item);
        when(f.offerings.validateCustomizations(item,Map.of())).thenReturn(Map.of());
        var response = f.service.confirm(new WhatsAppConfirmationRequest(
            WhatsAppConfirmationRequest.Type.OFFERING,9L,new BigDecimal("49.90"),Map.of()));
        assertTrue(response.url().startsWith("https://wa.me/5547999999999?text="));
        assertTrue(response.message().contains("Oficina exemplo"));
        assertTrue(response.message().contains("49,90"));
        verify(f.offerings,times(1)).publicOne(9L);
        verify(f.offerings,times(1)).validateCustomizations(item,Map.of());
    }

    @Test
    void stalePriceNeverBuildsContactLink() {
        var f = fixture();
        var p = new ProductResponse(12L,"Kit exemplo",null,new BigDecimal("120.00"),
            true,true,null,List.of(),List.of(),new BigDecimal("99.90"),
            ProductPriceStatus.CONFIRMED,2,null);
        when(f.catalog.published(12L)).thenReturn(p);
        assertThrows(BusinessException.class,()->f.service.confirm(new WhatsAppConfirmationRequest(
            WhatsAppConfirmationRequest.Type.PRODUCT,12L,new BigDecimal("98.90"),Map.of())));
        verifyNoInteractions(f.store);
        verify(f.catalog,times(1)).published(12L);
    }

    @Test
    void unknownProductPriceNeverCreatesAContactLink() {
        var f=fixture();
        var p=new ProductResponse(12L,"Produto sem preço",null,null,true,true,
            null,List.of(),List.of(),null,ProductPriceStatus.ON_REQUEST,0,null);
        when(f.catalog.published(12L)).thenReturn(p);
        assertThrows(BusinessException.class,()->f.service.forProduct(12L));
        assertThrows(BusinessException.class,()->f.service.confirm(new WhatsAppConfirmationRequest(
            WhatsAppConfirmationRequest.Type.PRODUCT,12L,BigDecimal.ZERO,Map.of())));
        verifyNoInteractions(f.store);
    }
}
