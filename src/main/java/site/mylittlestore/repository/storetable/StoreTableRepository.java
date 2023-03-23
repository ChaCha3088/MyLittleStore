package site.mylittlestore.repository.storetable;

import org.springframework.data.jpa.repository.JpaRepository;
import site.mylittlestore.domain.StoreTable;

public interface StoreTableRepository extends JpaRepository<StoreTable, Long> {
}
