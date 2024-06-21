# Tugas 2 PBO - Backend API Sistem Pembayaran main.java.com.subscription.models.Subscription Sederhana

Anggota:

Jeaconia Elfrida Tiono (2305551094)

Revito Manuel Hasudungan Manik (2305551130)

# Subscription Film Berbasis Java

Backend aplikasi subscription film berbasis Java dirancang untuk mengelola dan menyediakan layanan berlangganan film secara efisien dan aman. Database yang digunakan adalah SQLite untuk penyimpanan data. Postman digunakan sebagai alat untuk melakukan pengujian API, memastikan bahwa setiap endpoint berfungsi dengan baik sesuai dengan spesifikasi yang ditentukan. Melalui Postman, pengembang dapat menguji berbagai skenario penggunaan seperti pendaftaran pengguna baru, sehingga memastikan sistem backend siap digunakan oleh aplikasi front-end dan pengguna akhir.

# Dokumentasi Backend API
Di bawah merupakan tampilan pada postman jika diberikan perintah GET customers dengan menggunakan http://localhost:9094/customers ![1 - GET Customers  Melihat Daftar Semua Pelanggan](https://github.com/jeaconia/IFLY/assets/146644799/11afd45b-8868-4808-b2db-6cd73e25a619)

Di bawah merupakan tampilan pada postman jika diberikan perintah GET customers dengan id 11 dengan menggunakan http://localhost:9094/customers/11 
![2 - GET Customers  Melihat Informasi Pelanggan Berdasarkan ID](https://github.com/jeaconia/IFLY/assets/146644799/cb195ccf-240a-4540-8803-70662d808e28)

Di bawah merupakan tampilan pada postman jika diberikan perintah GET customers dengan id 11 dan informasi kartunya dengan menggunakan http://localhost:9094/customers/11/cards ![3 - GET Customers  Melihat Daftar Kartu Milik Pelanggan Berdasarkan ID](https://github.com/jeaconia/IFLY/assets/146644799/113085d4-9dc2-413a-8c92-e7ec4fcb1490)

Di bawah merupakan tampilan pada postman jika diberikan perintah GET customers dengan id 11 dan informasi berlangganannya dengan menggunakan http://localhost:9094/customers/11/subscriptions
![4 - GET Customers  Melihat Daftar Semua Subscriptions Milik Pelanggan Berdasarkan ID](https://github.com/jeaconia/IFLY/assets/146644799/ed1c507b-a20b-4e5b-a5a5-b1497d56f21a)

Di bawah merupakan tampilan pada postman jika diberikan perintah GET customers dengan id 12 dan informasi berlangganannya jika aktif dengan menggunakan http://localhost:9094/customers/12/subscriptions?subscriptions_status=active
![5 - GET Customers  Melihat Daftar Semua Subscriptions Milik Pelanggan yang Memiliki Status Tertentu](https://github.com/jeaconia/IFLY/assets/146644799/72352621-5b63-45ab-b77d-720a6561edca)

Di bawah merupakan tampilan pada postman jika diberikan perintah GET subscriptions dengan menggunakan http://localhost:9094/subscriptions
![6 - GET Subscriptions  Melihat Daftar Semua Subscriptions](https://github.com/jeaconia/IFLY/assets/146644799/cc453eae-2b91-4dda-9e41-aaff90837417)

Di bawah merupakan tampilan pada postman jika diberikan perintah GET subscriptions yang diurutkan mengikuti waktu dengan menggunakan http://localhost:9094/subscriptions?sort_by=current_term_end&sort_type=desc
![7 - GET Subscriptions  Melihat Daftar Semua Subscriptions yang Diurutkan Berdasarkan current_term_end Secara DESC](https://github.com/jeaconia/IFLY/assets/146644799/b5b5b6ea-eb0f-46c5-9e60-a412900daeec)

Di bawah merupakan tampilan pada postman jika diberikan perintah GET subscriptions dengan id 1 dengan menggunakan http://localhost:9094/subscriptions/1
![8 - GET Subscriptions  Melihat Informasi Subscriptions Berdasarkan ID](https://github.com/jeaconia/IFLY/assets/146644799/a61750d3-3869-4a1b-a7f5-17bc8a8db9ef)

Di bawah merupakan tampilan pada postman jika diberikan perintah GET items dengan menggunakan http://localhost:9094/items
![9 - GET Items  Melihat Daftar Semua Produk](https://github.com/jeaconia/IFLY/assets/146644799/2ccdcf13-4fda-492e-ad43-7cae8261c1f4)

Di bawah merupakan tampilan pada postman jika diberikan perintah GET items yang aktif dengan menggunakan http://localhost:9094/items?is_active=true
![10 - GET Items  Melihat Daftar Semua Produk yang Berstatus Aktif](https://github.com/jeaconia/IFLY/assets/146644799/e29e8926-7032-4dd1-b3dd-86221de418df)

Di bawah merupakan tampilan pada postman jika diberikan perintah GET items dengan id 1 dengan menggunakan http://localhost:9094/items/1
![11 - GET Items  Melihat Informasi Produk Berdasarkan ID](https://github.com/jeaconia/IFLY/assets/146644799/3ca26bd8-533d-4ada-86dc-6106a0df8227)

Di bawah merupakan tampilan pada postman jika diberikan perintah API POST untuk menambahkan data pelanggan dengan menggunakan http://localhost:9094/customers
![12 - API POST  Menambahkan Data Pelanggan Baru](https://github.com/jeaconia/IFLY/assets/146644799/f34f6b04-754f-4f96-966a-cd4fb40fe212)

Di bawah merupakan tampilan pada postman jika diberikan perintah API POST untuk menambahkan data berlangganan dengan menggunakan http://localhost:9094/subscriptions
![13 - API POST  Membuat Subscriptions Baru Beserta Atributnya](https://github.com/jeaconia/IFLY/assets/146644799/2db43f1e-01be-487c-8900-8dc261b604e4)

Di bawah merupakan tampilan pada postman jika diberikan perintah API POST untuk menambahkan data item dengan menggunakan http://localhost:9094/items
![14 - API POST  Menambahkan Data Produk Baru](https://github.com/jeaconia/IFLY/assets/146644799/98b8c26f-e01b-4f17-97a3-f3d4ce271545)

Di bawah merupakan tampilan pada postman jika diberikan perintah API PUT untuk mengupdate data pelanggan dengan menggunakan http://localhost:9094/customers/11
![15 - API PUT  Update Data Salah Satu Pelanggan Berdasarkan ID](https://github.com/jeaconia/IFLY/assets/146644799/0444923b-1a50-42d4-8f11-1b7f5bc67072)

Di bawah merupakan tampilan pada postman jika diberikan perintah API PUT untuk mengupdate data alamat pengiriman pelanggan dengan menggunakan http://localhost:9094/customers/11/shipping_addresses/1
![16 - API PUT  Update Data Alamat Pengiriman Pelanggan Berdasarkan ID](https://github.com/jeaconia/IFLY/assets/146644799/66220150-00da-4a24-81d9-aed7c9ca2240)

Di bawah merupakan tampilan pada postman jika diberikan perintah API PUT untuk mengupdate data items dengan menggunakan http://localhost:9094/items/1
![17 - API PUT  Update Data Salah Satu Produk Berdasarkan ID](https://github.com/jeaconia/IFLY/assets/146644799/3d1dc51c-c82a-4a4a-a10a-4c7a566c4eef)

Di bawah merupakan tampilan pada postman jika diberikan perintah API DELETE untuk menghapus data item dengan menggunakan http://localhost:9094/items/11
![18 - API DELETE  Menghapus Salah Satu Produk Berdasarkan ID](https://github.com/jeaconia/IFLY/assets/146644799/5e74be17-898f-41ae-bcd2-9be5d7d95c86)

Di bawah merupakan tampilan pada postman jika diberikan perintah API DELETE untuk menghapus data kartu customers dengan menggunakan http://localhost:9094/customers/20/cards/11
![19 - API DELETE  Menghapus Kartu Pelanggan yang Bukan Merupakan Kartu Utama](https://github.com/jeaconia/IFLY/assets/146644799/948ee1d0-3519-4373-a148-a21030d3d94d)

Di bawah merupakan tampilan pada postman jika keluaran ERROR 404
![20  Tampilan Error 404](https://github.com/jeaconia/IFLY/assets/146644799/21e06001-e74c-4c90-a179-9c87dd72730b)

Di bawah merupakan tampilan pada postman jika keluaran ERROR 404 BAD REQUEST
![21  Tampilan Error 400 BAD REQUEST](https://github.com/jeaconia/IFLY/assets/146644799/34b6dc27-e061-45a4-a9f8-61558b38d4a9)
