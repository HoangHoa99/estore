package assessment.estore.repository;

import assessment.estore.model.dao.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, UUID> {

    @Modifying
    @Query(nativeQuery = true,
            value = "DELETE FROM discount WHERE id = :discountId")
    int deleteDiscount(@Param("discountId") UUID discountId);

    List<Discount> findActiveDiscountsByProductId(UUID productId);
}
