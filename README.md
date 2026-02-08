# Diotviet (server)

Diotviet server

## Tech stack

&#9989; <strong>Spring</strong> &#8658; `6`

&#9989; <strong>Java</strong> &#8658; `19`

&#9989; <strong>Maven</strong> &#8658; `3.8.6`

&#9989; <strong>PostgreSQL</strong> &#8658; `12.12`

## Configuration
Create .env file
```bash
cp .env.example .env
```
Define these envs below
* DIOTVIET_APP_JWT_SECRET=[random_string]
* DIOTVIET_APP_JWT_EXPIRATION=[int]
* DIOTVIET_APP_TICKET_SECRET=[random_string]
* 
## Start app
```bash
docker-compose up -d --build
```

## Preset
Access [adminer](http://localhost:8081) to insert preset data

### Connection info
* **System**: PostgreSQL
* **Server**: database
* **Username**: postgres
* **Password**: 123456
* **Database**: other

```sql
INSERT INTO diotviet.users (id, name, password, role, username) VALUES (1, 'admin', '$2a$10$6Vnw2RKtja1x0rULNtBeHu8gK15qy7JhoMQzL6rJnLGpCtmosIvzK', 0, 'ahihi@gmail.com');

INSERT INTO diotviet.customers ("id", "address", "birthday", "code", "created_at", "created_by", "description", "email", "facebook", "is_deleted", "is_male", "last_order_at", "last_transaction_at", "name", "phone_number", "point", "version", "category_id") VALUES (1,	NULL,	NULL,	'KH00001',	'2026-02-07 22:09:38.088185',	'admin',	NULL,	NULL,	NULL,	'0',	'1',	NULL,	NULL,	'John Doe',	'0987654321',	NULL,	0,	6);

INSERT INTO diotviet.categories (id, name, type) VALUES (1, 'Sản phẩm', 0);
INSERT INTO diotviet.categories (id, name, type) VALUES (2, 'Dịch vụ', 0);
INSERT INTO diotviet.categories (id, name, type) VALUES (3, 'Combo', 0);
INSERT INTO diotviet.categories (id, name, type) VALUES (4, 'Đơn hàng', 1);
INSERT INTO diotviet.categories (id, name, type) VALUES (5, 'Giao dịch', 1);
INSERT INTO diotviet.categories (id, name, type) VALUES (6, 'Khách hàng', 2);
INSERT INTO diotviet.categories (id, name, type) VALUES (7, 'Nhân viên', 2);
INSERT INTO diotviet.categories (id, name, type) VALUES (8, 'Tài liệu', 3);

INSERT INTO diotviet.groups (id, name, type) VALUES (1, 'print_order', 3);
INSERT INTO diotviet.groups (id, name, type) VALUES (2, 'print_receipt', 3);
INSERT INTO diotviet.groups (id, name, type) VALUES (3, 'print_import', 3);
INSERT INTO diotviet.groups (id, name, type) VALUES (4, 'print_export', 3);
INSERT INTO diotviet.groups (id, name, type) VALUES (6, 'GIẶT HẤP', 0);
INSERT INTO diotviet.groups (id, name, type) VALUES (8, 'GIẶT ƯỚT', 0);
INSERT INTO diotviet.groups (id, name, type) VALUES (10, 'ỦI', 0);
INSERT INTO diotviet.groups (id, name, type) VALUES (5, 'Giặt + Sấy + Gấp', 0);
INSERT INTO diotviet.groups (id, name, type) VALUES (7, 'GIẶT SẤY', 0);
INSERT INTO diotviet.groups (id, name, type) VALUES (9, 'KHUYẾN MÃI', 0);
INSERT INTO diotviet.groups (id, name, type) VALUES (11, 'print_ticket', 3);

INSERT INTO diotviet.documents (id, content, is_active, name, version, group_id) VALUES (2, '<div class="printBox"><table style="width:100%"><tbody><tr><td style="text-align:center"><table style="width:100%"><tbody><tr><td style="text-align:center"><span style="font-size:22px"><strong><span style="font-family:''Times New Roman'',Times,serif"><em>Shop</em></span></strong></span></td></tr><tr><td style="text-align:center"><strong><span style="font-size:12px">Address</span></strong></td></tr><tr><td style="font-size:11px;text-align:center"><span style="font-size:18px"><strong>Tel: Phone number</strong></span></td></tr></tbody></table></td></tr></tbody></table><div style="padding:10px 0 0;text-align:center"><span id="u-fe2906c4-2e31-4a52-9e93-bd5298c62b07-id_qr" class="printable-tag simple-tag">{{Identify_QR code}}</span></div><div style="padding:10px 0 0;text-align:center"><table style="border-collapse:collapse;width:100%" border="1"><colgroup><col style="width:17.229%"><col style="width:82.8916%"></colgroup><tbody><tr><td style="text-align:left">Kh&aacute;ch h&agrave;ng</td><td style="text-align:left"><span id="u-ba2f4e4d-6c49-4d66-875b-ce4b18d90499-ticket_name" class="printable-tag simple-tag">{{Customer_name}}</span></td></tr><tr><td style="text-align:left">Số điện thoại</td><td style="text-align:left"><span id="u-a28eee19-3ef2-4ff5-b21e-1de03fa394fd-ticket_phoneNumber" class="printable-tag simple-tag">{{Customer_phone number}}</span></td></tr></tbody></table></div></div>', true, 'Hóa đơn', 2, 2);
INSERT INTO diotviet.documents (id, content, is_active, name, version, group_id) VALUES (3, '<div class="printBox"><table style="width:100%"><tbody><tr><td style="text-align:center"><table style="width:100%"><tbody><tr><td style="text-align:center"><span style="font-size:22px"><strong><span style="font-family:''Times New Roman'',Times,serif"><em>Shop</em></span></strong></span></td></tr><tr><td style="text-align:center"><strong><span style="font-size:12px">Address</span></strong></td></tr><tr><td style="font-size:11px;text-align:center"><span style="font-size:18px"><strong>Tel: Phone number</strong></span></td></tr></tbody></table></td></tr></tbody></table><div style="padding:10px 0 0;text-align:center"><span id="u-fe2906c4-2e31-4a52-9e93-bd5298c62b07-id_qr" class="printable-tag simple-tag">{{Identify_QR code}}</span></div><div style="padding:10px 0 0;text-align:center"><table style="border-collapse:collapse;width:100%" border="1"><colgroup><col style="width:17.229%"><col style="width:82.8916%"></colgroup><tbody><tr><td style="text-align:left">Kh&aacute;ch h&agrave;ng</td><td style="text-align:left"><span id="u-ba2f4e4d-6c49-4d66-875b-ce4b18d90499-ticket_name" class="printable-tag simple-tag">{{Customer_name}}</span></td></tr><tr><td style="text-align:left">Số điện thoại</td><td style="text-align:left"><span id="u-a28eee19-3ef2-4ff5-b21e-1de03fa394fd-ticket_phoneNumber" class="printable-tag simple-tag">{{Customer_phone number}}</span></td></tr></tbody></table></div></div>', true, 'Nhập hàng', 1, 4);
INSERT INTO diotviet.documents (id, content, is_active, name, version, group_id) VALUES (4, '<div class="printBox"><table style="width:100%"><tbody><tr><td style="text-align:center"><table style="width:100%"><tbody><tr><td style="text-align:center"><span style="font-size:22px"><strong><span style="font-family:''Times New Roman'',Times,serif"><em>Shop</em></span></strong></span></td></tr><tr><td style="text-align:center"><strong><span style="font-size:12px">Address</span></strong></td></tr><tr><td style="font-size:11px;text-align:center"><span style="font-size:18px"><strong>Tel: Phone number</strong></span></td></tr></tbody></table></td></tr></tbody></table><div style="padding:10px 0 0;text-align:center"><span id="u-fe2906c4-2e31-4a52-9e93-bd5298c62b07-id_qr" class="printable-tag simple-tag">{{Identify_QR code}}</span></div><div style="padding:10px 0 0;text-align:center"><table style="border-collapse:collapse;width:100%" border="1"><colgroup><col style="width:17.229%"><col style="width:82.8916%"></colgroup><tbody><tr><td style="text-align:left">Kh&aacute;ch h&agrave;ng</td><td style="text-align:left"><span id="u-ba2f4e4d-6c49-4d66-875b-ce4b18d90499-ticket_name" class="printable-tag simple-tag">{{Customer_name}}</span></td></tr><tr><td style="text-align:left">Số điện thoại</td><td style="text-align:left"><span id="u-a28eee19-3ef2-4ff5-b21e-1de03fa394fd-ticket_phoneNumber" class="printable-tag simple-tag">{{Customer_phone number}}</span></td></tr></tbody></table></div></div>', true, 'Xuất hàng', 1, 3);
INSERT INTO diotviet.documents (id, content, is_active, name, version, group_id) VALUES (1, '<div class="printBox"><table style="width:100%"><tbody><tr><td style="text-align:center"><table style="width:100%"><tbody><tr><td style="text-align:center"><span style="font-size:22px"><strong><span style="font-family:''Times New Roman'',Times,serif"><em>Shop</em></span></strong></span></td></tr><tr><td style="text-align:center"><strong><span style="font-size:12px">Address</span></strong></td></tr><tr><td style="font-size:11px;text-align:center"><span style="font-size:18px"><strong>Tel: Phone number</strong></span></td></tr></tbody></table></td></tr></tbody></table><div style="padding:10px 0 0;text-align:center"><strong><span id="u-55cdcf85-7964-4098-811c-3f740844d368-id_bc" class="printable-tag simple-tag">{{Identify_Barcode}}</span></strong></div><div style="padding:10px 0 0;text-align:center"><span style="font-size:20px"><strong><span id="u-f50f27ab-053b-40b5-9020-5b483a1bf1bf-id_raw" class="printable-tag simple-tag">{{Identify_number}}</span></strong></span></div><table style="margin:5px 0 5px;width:100%"><tbody><tr><td style="font-size:11px;text-align:center"><span id="u-e17267a5-c4d1-462b-ad27-4100a4abdf35-order_createdAt" class="printable-tag simple-tag">{{Created_at}}</span></td></tr><tr><td style="font-size:11px;text-align:center"><span style="font-size:26px"><strong><span id="u-51f38c72-1822-42a6-bfce-c5604818566b-customer_name" class="printable-tag simple-tag">{{Customer_name}}</span></strong></span><br><span style="font-size:24px"><span id="u-b66f8005-f27c-4b56-b226-fb5e3ee22af5-order_phoneNumber" class="printable-tag simple-tag">{{Phone_number}}</span></span></td></tr><tr><td style="font-size:11px">&nbsp;</td></tr><tr><td style="font-size:11px"><span style="font-size:11px">Người lập phiếu:<span id="u-f99ea449-d76f-4208-9e0a-4462970f236d-order_createdBy" class="printable-tag simple-tag">{{Created_by}}</span></span></td></tr></tbody></table><table id="order_items" class="wrapping-table" style="width:98%;height:67.2px" cellpadding="1"><tbody><tr style="height:22.4px"><td style="border-bottom:1px solid #000;border-top:1px solid #000;width:35%;height:22.4px"><span style="font-size:11px"><strong>Dịch vụ&nbsp;</strong></span></td><td style="border-bottom:1px solid #000;border-top:1px solid #000;text-align:right;width:30%;height:22.4px"><span style="font-size:11px"><strong>SL</strong></span></td><td style="border-bottom:1px solid #000;border-top:1px solid #000;text-align:right;height:22.4px"><span style="font-size:11px"><strong>T.Tiền</strong></span></td></tr><tr class="printable-row iterable-row order_items" style="height:22.4px"><td style="padding-top:3px;height:22.4px" colspan="3"><span style="font-size:11px"><span id="u-20367aea-e75c-4bc8-b54f-8ce188cc745d-item_title" class="printable-tag iterable-tag order_items">{{Product_name}}</span></span></td></tr><tr class="printable-row iterable-row order_items" style="height:22.4px"><td style="border-bottom:1px dashed #000;height:22.4px"><span style="font-size:11px"><span id="u-7cc1a089-9229-454e-bf4a-ef48b237ef0f-item_note" class="printable-tag iterable-tag order_items">{{Note}}</span></span></td><td style="border-bottom:1px dashed #000;text-align:right;height:22.4px"><span style="font-size:11px"><span id="u-7f86da1c-bc81-4ddd-97b8-cde2c9c7bf1d-item_quantity" class="printable-tag iterable-tag order_items">{{Quantity}}</span></span></td><td style="border-bottom:1px dashed #000;text-align:right;height:22.4px"><span style="font-size:11px"><span id="u-084ab2a3-f8b4-44d1-a94b-77bd782a227a-item_actualPrice" class="printable-tag iterable-tag order_items">{{Actual_price}}</span></span></td></tr></tbody></table><table style="margin-top:px;width:100%"><tbody><tr><td style="font-size:9px;font-style:italic;text-align:center"><span style="font-size:24px"><strong><span id="u-5837ce5a-112a-4209-929e-63ce7feb23b3-order_paymentAmount" class="printable-tag simple-tag">{{Payment_amount}}</span>đ</strong></span></td></tr><tr><td style="font-size:9px;font-style:italic;text-align:center">&nbsp;</td></tr><tr><td style="font-size:11px;font-style:italic;text-align:center">Ch&acirc;n th&agrave;nh cảm ơn qu&yacute; kh&aacute;ch !</td></tr></tbody></table></div>', true, 'Đơn hàng', 4, 1);
INSERT INTO diotviet.documents (id, content, is_active, name, version, group_id) VALUES (5, '<div class="printBox"><table style="width:100%"><tbody><tr><td style="text-align:center"><table style="width:100%"><tbody><tr><td style="text-align:center"><span style="font-size:22px"><strong><span style="font-family:''Times New Roman'',Times,serif"><em>Shop</em></span></strong></span></td></tr><tr><td style="text-align:center"><strong><span style="font-size:12px">Address</span></strong></td></tr><tr><td style="font-size:11px;text-align:center"><span style="font-size:18px"><strong>Tel: Phone number</strong></span></td></tr></tbody></table></td></tr></tbody></table><div style="padding:10px 0 0;text-align:center"><span id="u-fe2906c4-2e31-4a52-9e93-bd5298c62b07-id_qr" class="printable-tag simple-tag">{{Identify_QR code}}</span></div><div style="padding:10px 0 0;text-align:center"><table style="border-collapse:collapse;width:100%" border="1"><colgroup><col style="width:17.229%"><col style="width:82.8916%"></colgroup><tbody><tr><td style="text-align:left">Kh&aacute;ch h&agrave;ng</td><td style="text-align:left"><span id="u-ba2f4e4d-6c49-4d66-875b-ce4b18d90499-ticket_name" class="printable-tag simple-tag">{{Customer_name}}</span></td></tr><tr><td style="text-align:left">Số điện thoại</td><td style="text-align:left"><span id="u-a28eee19-3ef2-4ff5-b21e-1de03fa394fd-ticket_phoneNumber" class="printable-tag simple-tag">{{Customer_phone number}}</span></td></tr></tbody></table></div></div>', true, 'Vé hàng', 2, 11);
```
