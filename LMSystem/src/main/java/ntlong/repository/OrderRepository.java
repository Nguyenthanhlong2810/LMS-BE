package ntlong.repository;

import ntlong.model.OrderUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderUser,Long> {
    Optional<OrderUser> findOrderUserByTransactionId(String transactionId);
}
