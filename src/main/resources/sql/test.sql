SET REFERENTIAL_INTEGRITY FALSE; --제약조건 무효화

TRUNCATE TABLE member RESTART IDENTITY;
TRUNCATE TABLE stores RESTART IDENTITY;
TRUNCATE TABLE store_table RESTART IDENTITY;
TRUNCATE TABLE orders RESTART IDENTITY;
TRUNCATE TABLE order_item RESTART IDENTITY;
TRUNCATE TABLE payment RESTART IDENTITY;
TRUNCATE TABLE item RESTART IDENTITY;

SET REFERENTIAL_INTEGRITY TRUE; --제약조건 재설정