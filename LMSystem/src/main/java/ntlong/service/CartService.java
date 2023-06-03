package ntlong.service;

import ntlong.dto.CartDTO;
import ntlong.model.Cart;

import java.util.List;

public interface CartService {
    void addProduct(Long courseID, String username);

    void removeProducts(List<Long> courseIDs, String username);

    CartDTO getCart(String username);

    void removeProduct(Long courseID, String username);
}
