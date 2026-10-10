-- MySQL dump 10.13  Distrib 9.1.0, for Win64 (x86_64)
--
-- Host: localhost    Database: rishtabox
-- ------------------------------------------------------
-- Server version	8.0.40

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES ('artificial-jewellery','Artificial Jewellery','Stylish artificial jewellery for every occasion','/uploads/categories/f8e4cd86-85b3-4c6c-af13-24144fbe8ea1.jpg',_binary ''),('birthday-return-gifts','Birthday Return Gifts','Special return gifts for birthday celebrations','/uploads/categories/915cddf8-5e4e-4fd0-a880-f82164148aca.jpg',_binary ''),('customize-coffee-mug','Customize Coffee Mug','Personalized coffee mugs for every occasion','/uploads/categories/9603884c-5299-470b-8815-a2014379f8cd.jpg',_binary ''),('groom-mala','Groom Mala','Beautiful malas for the groom','/uploads/categories/60ebaad6-4a0e-4fd7-b483-267a1e932f0f.jpg',_binary '');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `festivals`
--

LOCK TABLES `festivals` WRITE;
/*!40000 ALTER TABLE `festivals` DISABLE KEYS */;
INSERT INTO `festivals` VALUES ('1','Diwali','/uploads/festivals/b9392df9-44c9-4721-8e13-389d0e9d3285.jpg','Festival of lights and celebrations',_binary ''),('10','Lohri','/uploads/festivals/23a5f216-c145-4910-9bce-ccc68acaae35.jpg','Popular winter harvest festival',_binary '\0'),('11','Christmas','/uploads/festivals/24eba565-b2a9-4760-89ef-18f40046cf46.jpg','Celebration of the birth of Jesus Christ',_binary '\0'),('12','Navroz','/uploads/festivals/25610573-be68-49f4-bebd-d4c888887db0.jpg','Parsi New Year celebration',_binary '\0'),('13','New Year','/uploads/festivals/23baa50e-5acd-4065-9a44-8b95564e6638.jpg','Celebrate the beginning of a new year',_binary '\0'),('2','Holi','/uploads/festivals/abc87c5f-8306-4382-a24b-bb7d5d88c585.jpg','Festival of colors, joy and togetherness',_binary '\0'),('3','Dussehra','/uploads/festivals/24efe061-389b-409c-874b-588d7e03517e.jpg','Festival celebrating the victory of good over evil',_binary ''),('4','Navratri','/uploads/festivals/7d8e9993-fd05-4901-8793-493f1aaa3ede.jpg','Nine nights of devotion and celebration',_binary ''),('5','Ganesh Chaturthi','/uploads/festivals/a3975919-ee2b-46cc-a160-94929c966a71.jpg','Festival celebrating Lord Ganesha',_binary '\0'),('6','Chhath Puja','/uploads/festivals/b6cad1eb-42bc-4925-9fc8-4130e17c3acf.jpg','Traditional gifts for Chhath Puja celebrations',_binary ''),('7','Eid-ul-Fitr','/uploads/festivals/e06d383a-b7ac-4b01-9420-48e74a7940a0.jpg','Festival celebrated at the end of Ramadan',_binary '\0'),('8','Eid-ul-Adha','/uploads/festivals/8a03c2cd-792e-4a95-9257-b63813a320db.jpg','Festival of sacrifice, sharing and togetherness',_binary '\0'),('9','Ramadan','/uploads/festivals/be3937c2-02e3-4ce5-8f87-70263d333208.jpg','Holy month of fasting, prayer and reflection',_binary '\0');
/*!40000 ALTER TABLE `festivals` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `relationships`
--

LOCK TABLES `relationships` WRITE;
/*!40000 ALTER TABLE `relationships` DISABLE KEYS */;
INSERT INTO `relationships` VALUES ('brother','/uploads/relationships/e96f2529-cc31-42e8-9500-1c8b7148cfb2.jpg','Gifts for Brother','Cool and thoughtful gifts for brother',_binary ''),('couples','/uploads/relationships/f7521dd4-ac96-4658-a60f-dee4095d3323.jpg','Gifts for Couples','Perfect gifts for special couples',_binary ''),('dad','/uploads/relationships/df2bf5b9-db35-49f2-b5ab-3ae2d22abf85.jpg','Gifts for Dad','Meaningful gifts for your dad',_binary ''),('friend','/uploads/relationships/295a7791-dc9b-4b33-a74c-3435e5de11c5.jpg','Gifts for Friends','Special gifts for your best friends',_binary ''),('husband','/uploads/relationships/a26e1d2e-ae0f-4c3a-9d1a-e9b793934bfa.jpg','Gifts for Husband','Special gifts for your husband',_binary '\0'),('mom','/uploads/relationships/21d0176c-4e9b-4920-bbbc-233b7bac2f21.jpg','Gifts for Mom','Heartwarming gifts for your mom',_binary '\0'),('sister','/uploads/relationships/b5cb297b-79a9-4fd7-bf9e-a2321b98583d.jpg','Gifts for Sister','Beautiful gifts for your loving sister',_binary '\0'),('wife','/uploads/relationships/ee00b924-8f1d-47ff-b1d1-616d0ae1b8d7.jpg','Gifts for Wife','Beautiful gifts for your wife',_binary '\0');
/*!40000 ALTER TABLE `relationships` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,'Personalized coffee mug gift for Brother','/uploads/products/52992798-3f75-4749-9341-56f3c067d015.jpg','Brother Coffee Mug',699,399,50,NULL,NULL,'brother',_binary ''),(2,'Personalized coffee mug gift for Couples','images/mug/CoupleMug.jpg','Couple Coffee Mug',699,399,32,NULL,NULL,'couples',_binary ''),(3,'Personalized coffee mug gift for Dad','/uploads/products/b5ed3643-b893-4e2b-b159-56294fec70a2.jpg','Dad Coffee Mug',699,399,49,NULL,NULL,'dad',_binary ''),(4,'Personalized coffee mug gift for Friends','images/mug/FriendMug.jpg','Friend Coffee Mug',699,399,49,NULL,NULL,'friend',_binary ''),(5,'Personalized coffee mug gift for Husband','images/mug/RelationshipMug.jpg','Husband Coffee Mug',699,399,50,NULL,NULL,'husband',_binary ''),(6,'Personalized coffee mug gift for Mom','/uploads/products/75541d25-dee1-4e1b-a755-14bc2111ff56.jpg','Mom Coffee Mug',699,399,49,NULL,NULL,'mom',_binary ''),(7,'Personalized coffee mug gift for Sister','images/mug/Sister.jpg','Sister Coffee Mug',699,399,50,NULL,NULL,'sister',_binary ''),(8,'Personalized coffee mug gift for Wife','images/mug/RelationshipMug.jpg','Wife Coffee Mug',699,399,50,NULL,NULL,'wife',_binary ''),(9,'Special personalized coffee mug gift for Diwali','/uploads/products/a7a22d63-35fd-4837-ae20-1d9be3678558.jpg','Diwali Coffee Mug',699,399,49,NULL,'1',NULL,_binary ''),(10,'Colorful personalized coffee mug gift for Holi','images/mug/FestivalMug.jpg','Holi Coffee Mug',699,399,50,NULL,'2',NULL,_binary ''),(11,'Special personalized coffee mug gift for Dussehra','images/mug/FestivalMug.jpg','Dussehra Coffee Mug',699,399,50,NULL,'3',NULL,_binary ''),(12,'Special personalized coffee mug gift for Navratri','images/mug/MugDuo.jpg','Navratri Coffee Mug',699,399,50,NULL,'4',NULL,_binary ''),(13,'Special personalized coffee mug gift for Ganesh Chaturthi','/uploads/products/4c5f792b-c87b-407f-a95f-6daa83f5a66e.jpg','Ganesh Chaturthi Coffee Mug',699,399,50,NULL,'5',NULL,_binary ''),(14,'Special personalized coffee mug gift for Chhath Puja','images/mug/MugDuo.jpg','Chhath Puja Coffee Mug',699,399,50,NULL,'6',NULL,_binary ''),(15,'Special personalized coffee mug gift for Eid-ul-Fitr','images/mug/FestivalMug.jpg','Eid-ul-Fitr Coffee Mug',699,399,50,NULL,'7',NULL,_binary ''),(16,'Special personalized coffee mug gift for Eid-ul-Adha','/uploads/products/7c99c213-97b2-4347-b9cc-2cb3aaf17c47.jpg','Eid-ul-Adha Coffee Mug',699,399,50,NULL,'8',NULL,_binary ''),(17,'Special personalized coffee mug gift for Ramadan','images/mug/FestivalMug.jpg','Ramadan Coffee Mug',699,399,50,NULL,'9',NULL,_binary ''),(18,'Special personalized coffee mug gift for Lohri','images/mug/CommonMug.jpg','Lohri Coffee Mug',699,399,50,NULL,'10',NULL,_binary ''),(19,'Special personalized coffee mug gift for Christmas','images/mug/FestivalMug.jpg','Christmas Coffee Mug',699,399,49,NULL,'11',NULL,_binary ''),(20,'Special personalized coffee mug gift for Navroz','images/mug/FestivalMug.jpg','Navroz Coffee Mug',699,399,49,NULL,'12',NULL,_binary ''),(21,'Special personalized coffee mug gift for New Year','images/mug/FestivalMug.jpg','New Year Coffee Mug',699,399,50,NULL,'13',NULL,_binary ''),(22,'Beautiful personalized coffee mug with an elegant jewellery theme','images/mug/FesMug.jpg','Jewellery Gift Mug',699,399,50,'artificial-jewellery',NULL,NULL,_binary ''),(23,'Cute personalized coffee mug perfect for birthday return gifts','images/mug/Sister.jpg','Birthday Return Gift Mug',699,399,50,'birthday-return-gifts',NULL,NULL,_binary ''),(24,'Personalized coffee mug with your favorite photo and memories','images/mug/RelationshipMug.jpg','Customize Photo Coffee Mug',699,399,50,'customize-coffee-mug',NULL,NULL,_binary ''),(25,'Beautiful personalized coffee mug with a stylish groom mala theme','images/mug/MugDuo.jpg','Groom Mala Gift Mug',699,399,50,'groom-mala',NULL,NULL,_binary '');
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `sliders`
--

LOCK TABLES `sliders` WRITE;
/*!40000 ALTER TABLE `sliders` DISABLE KEYS */;
INSERT INTO `sliders` VALUES (1,_binary '',1,'/uploads/sliders/b4976567-2616-4731-931c-d57d9a0919e6.jpeg',NULL,NULL,NULL),(2,_binary '',2,'/uploads/sliders/db033de3-c9b3-47f4-a9ad-ee5a2d353651.jpeg',NULL,NULL,NULL),(3,_binary '',3,'/uploads/sliders/9ccefacb-2262-4664-8184-5206109f289a.png',NULL,NULL,NULL),(4,_binary '',4,'/uploads/sliders/6dd050c2-af11-4d24-aa54-4c1de68bf134.png',NULL,NULL,NULL);
/*!40000 ALTER TABLE `sliders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `testimonials`
--

LOCK TABLES `testimonials` WRITE;
/*!40000 ALTER TABLE `testimonials` DISABLE KEYS */;
INSERT INTO `testimonials` VALUES (1,'https://images.unsplash.com/photo-1494790108377-be9c29b29330','Aditi Sharma','I ordered a birthday gift for my sister and she absolutely loved it. The product was nicely packed and delivered on time.',5,'PUBLISHED'),(2,'https://images.unsplash.com/photo-1500648767791-00dcc994a43e','Karan Mehta','Good experience overall. The product looked just like the pictures and the quality was better than I expected.',4,'PUBLISHED'),(3,'https://images.unsplash.com/photo-1531123897727-8f129e1688ce','Simran Kaur','I was looking for a thoughtful anniversary gift and found exactly what I needed on RishtaBox. Really happy with the purchase.',5,'PUBLISHED'),(5,'https://images.unsplash.com/photo-1488426862026-3ee34a7d66df','Nisha Gupta','This was my first order from RishtaBox and I am very satisfied. The gift arrived safely and made my friend really happy.',5,'PUBLISHED'),(6,'https://i.pravatar.cc/150?img=32','Priya Kapoor','Beautiful gift collection aur amazing packaging. Gift bilkul time par deliver hua.',5,'PUBLISHED'),(21,'/uploads/testimonials/1cab7034-2881-43aa-8d15-48b785ce0dde.jpg','Priya Kapoor','I ordered a birthday gift for my sister and she absolutely loved it. The product was nicely packed and delivered on time.',4,'PUBLISHED');
/*!40000 ALTER TABLE `testimonials` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `blogs`
--

LOCK TABLES `blogs` WRITE;
/*!40000 ALTER TABLE `blogs` DISABLE KEYS */;
INSERT INTO `blogs` VALUES (1,'RishtaBox','Finding the perfect wedding gift can be difficult. A thoughtful and meaningful gift can make the couple feel special and create beautiful memories.','2026-09-25 16:15:58.000000','Discover thoughtful and memorable wedding gifts for newly married couples.','https://images.unsplash.com/photo-1519225421980-715cb0215aed',_binary '','10 Best Wedding Gift Ideas for Couples'),(2,'RishtaBox','Finding the perfect wedding gift can be difficult. A thoughtful and meaningful gift can make the couple feel special and create beautiful memories. From personalized gifts to useful home accessories, there are many wonderful options for newly married couples.','2026-09-25 16:53:07.000000','Discover thoughtful and memorable wedding gifts for newly married couples.','/uploads/blogs/fcd66e6d-3afe-424f-b721-136a8d3c7caf.jpg',_binary '','10 Best Wedding Gift Ideas for Couples'),(3,'RishtaBox','A birthday is a beautiful opportunity to show someone how much they mean to you. Personalized photo gifts, flowers, accessories, chocolates and useful lifestyle products can make the celebration memorable. Choose a gift based on their personality and interests.','2026-09-25 16:53:07.000000','Make birthdays more special with thoughtful gifts chosen with love.','https://images.unsplash.com/photo-1530103862676-de8c9debad1d',_binary '','Best Birthday Gift Ideas for Your Loved Ones'),(4,'RishtaBox','Anniversaries are a wonderful reminder of the memories and moments shared together. Personalized gifts, couple hampers, romantic surprises and memorable keepsakes can make the day extra special. Choose something that reflects your journey together.','2026-09-25 16:53:07.000000','Celebrate your special relationship with meaningful anniversary gift ideas.','https://images.unsplash.com/photo-1516589178581-6cd7833ae3b2',_binary '','Perfect Anniversary Gifts to Celebrate Love'),(5,'RishtaBox','Diwali is a time for happiness, togetherness and sharing. Gift hampers, sweets, decorative items, personalized gifts and useful products are wonderful choices for family and friends. A thoughtful Diwali gift can make the celebration even more memorable.','2026-09-25 16:53:07.000000','Celebrate the festival of lights with beautiful and thoughtful Diwali gifts.','https://images.unsplash.com/photo-1606800052052-a08af7148866',_binary '','Diwali Gift Ideas for Family and Friends');
/*!40000 ALTER TABLE `blogs` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-07 15:26:13
