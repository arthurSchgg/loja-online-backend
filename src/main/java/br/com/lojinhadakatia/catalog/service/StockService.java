package br.com.lojinhadakatia.catalog.service;

import br.com.lojinhadakatia.catalog.dto.*;
import br.com.lojinhadakatia.catalog.entity.*;
import br.com.lojinhadakatia.catalog.repository.*;
import br.com.lojinhadakatia.shared.exception.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class StockService {
    private final ProductRepository products;
    private final ProductStockMovementRepository movements;

    public StockService(ProductRepository products, ProductStockMovementRepository movements) {
        this.products = products;
        this.movements = movements;
    }

    public StockBalanceResponse balance(Long productId) {
        var product = products.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado."));
        return new StockBalanceResponse(productId, product.getStockQuantity(),
            product.isAvailableForConsultation());
    }

    public Page<StockMovementResponse> history(Long productId, Pageable pageable) {
        if (!products.existsById(productId)) throw new ResourceNotFoundException("Produto não encontrado.");
        return movements.findByProductIdOrderByIdDesc(productId, pageable).map(this::toResponse);
    }

    @Transactional
    public StockMovementResponse record(Long productId, StockMovementRequest request, String actor) {
        Product product = products.findByIdForStockUpdate(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado."));
        int oldQuantity = product.getStockQuantity();
        int input = request.quantity();
        int result;
        switch (request.type()) {
            case ENTRY -> {
                if (input == 0) throw new BusinessException("Entrada deve ser maior que zero.");
                try { result = Math.addExact(oldQuantity, input); }
                catch (ArithmeticException ex) { throw new BusinessException("Limite de estoque excedido."); }
            }
            case EXIT -> {
                if (input == 0) throw new BusinessException("Saída deve ser maior que zero.");
                if (input > oldQuantity) throw new BusinessException("Estoque insuficiente.");
                result = oldQuantity - input;
            }
            case ADJUSTMENT -> result = input;
            default -> throw new BusinessException("Tipo de movimentação inválido.");
        }
        product.setStockQuantity(result);
        var movement = new ProductStockMovement(product, request.type(), input, result,
            request.reason().trim(), actor);
        product = products.saveAndFlush(product);
        return toResponse(movements.saveAndFlush(movement));
    }

    private StockMovementResponse toResponse(ProductStockMovement movement) {
        return new StockMovementResponse(movement.getId(), movement.getType(),
            movement.getQuantity(), movement.getResultingQuantity(),
            movement.getReason(), movement.getActor(), movement.getCreatedAt());
    }
}
