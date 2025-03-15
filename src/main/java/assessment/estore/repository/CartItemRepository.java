package assessment.estore.repository;

import assessment.estore.model.dao.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
    Optional<CartItem> findByCartIdAndProductId(UUID cartId, UUID productId);

    @Modifying
    @Query(nativeQuery = true,
            value = "DELETE FROM cart_item WHERE id = :cartItemId")
    int deleteCartItem(@Param("cartItemId") UUID cartItemId);
}
