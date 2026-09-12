-- MySQL dump 10.13  Distrib 8.0.38, for Win64 (x86_64)
--
-- Host: localhost    Database: internalbooks
-- ------------------------------------------------------
-- Server version	9.0.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `t_user`
--

DROP TABLE IF EXISTS `t_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user` (
  `user_id` int NOT NULL,
  `name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `mail_address` varchar(255) DEFAULT NULL,
  `role` int DEFAULT '0',
  `department_id` int DEFAULT NULL,
  `delete_flg` int DEFAULT 0,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user`
--

LOCK TABLES `t_user` WRITE;
/*!40000 ALTER TABLE `t_user` DISABLE KEYS */;
INSERT INTO `t_user` VALUES
('1', '加藤　真太郎', '$2a$10$A2NJ8Z8ttJbOFHyaM7IPXOeKi9aBpg42qckwHtuZPXgJ2cNilUAbK', 's.kato@grow-community.net', 0, 1, 0),
('2', '工藤　さよ子', '$2a$10$sBXoOHCMlU.fG2UuqaK89eoIsoyyYXU6Nf1edKurXhiSjD1RPZfiq', 's.kudo@start-it.co.jp', 0, 13, 0),
('3', '石川　里紗', '$2a$10$oHkY5woTj.nsnG/kjmnDR.4jo3xhioc9bbsSjuyMPTakE93KOTEqu', 'lmyp3lmrg51029@gmail.com', 0, 15, 0),
('4', '松木　育恵', '$2a$10$OthzL3aOTp.arr86JUGFguQ9TtYllUsngujxptwvhlTYSbJlsC392', 'ma12iku12@gmail.com', 0, 16, 0),
('5', '中野　孝平', '$2a$10$YEVY9A3aa5YUfuRWX9Dz6.iLicKPZpfeoWS4L9W9eLDU67W8qkbiC', 'khinkn0539@gmail.com', 0, 18, 0),
('6', '雨宮　裕樹', '$2a$10$.Rv1ae/Ru3qpFaVgWsiZb.OmgoBo8Zmhj6FhPoCoJHHdMqOtiiGHu', 'y.amemiya@grow-community.net', 0, 20, 0),
('100006', '牧　秀樹', '$2a$10$W7XEvSCczfp688vfMMVkQOLPlXPq3Bgd7XuV2iuHj56w0EZHj.GP2', 'm.hideki0425@icloud.com', 0, 17, 0),
('200000', '白石　和樹', '$2a$10$KrC.qItyBc8Cs2nHN6YRGOYzhl.Fj5cqbgk/YkUMEoUHEwDCWNmvG', 'shiraishi.kazuki715@gmail.com', 0, 6, 0),
('300000', '中原　健人', '$2a$10$M/FIdO8tHSP8macDb/LU..TVIr.OHsioDK35DNV1MrYBRIuvtIMDG', 'kento0605n@icloud.com', 0, 8, 0),
('300010', '山田　柊弥', '$2a$10$vHmBvWuaRM1ztr1uajcpKefKX2BDsjPKGimM.CvJNG/wZ6eAizL8u', 'touch.cheery.blossoms@gmail.com', 0, 10, 0),
('300015', '春日　勇人', '$2a$10$2hOq3Q.1qgxO8yXzxRnhzOfULakbRYhHApZqq2Yw07OMJnfB2dyvy', 'yuto.gasuka5170@gmail.com', 0, 12, 0),
('300016', '伊藤　正英', '$2a$10$agNzMxD12UyR1lrL6FPcxe4nQQgzdJvUH3k4.m2kfzworyuCcKTrS', 'masa110hide0530@gmail.com', 0, 12, 0),
('300017', '杉ノ下　大介', '$2a$10$eZnIzI/UugUXVacfucDBKuknHuCn6DBky38ocI.TrADSZ.EFP6jZ2', 'gio.sp400@gmail.com', 0, 11, 0),
('300019', '佐々木ブルノ　康多', '$2a$10$wActqCQc88pT388OGFFPJ.H3sOtf/VQJY93Bk9Ueq5rEt3gtzTGrO', 'brunoa0907@i.softbank.jp', 0, 5, 0),
('300026', '野元　結水', '$2a$10$8n02K96aiVHOEsFhJ6GFK.sg7DoHWVAcv9Z9uGzNelRATEKhtk.Hq', 'gamgam-0103@ezweb.ne.jp', 0, 10, 0),
('300028', '安川　創', '$2a$10$/5Q9ttPbHcxPK0PJ2LRNROSuOAoswMCB0jSLIZblY/ykvc4trzM4G', 'bocad39@icloud.com', 0, 10, 0),
('300030', '福山　練', '$2a$10$2n6OV8cK.FJPdIdAyJqj/uYWowrDgNcSRsy9wgwAoEjnbZaDV5LyK', 'renon99257@gmail.com', 0, 10, 0),
('300031', '佐藤　舜', '$2a$10$yyjkJ.kv5z/LpfVuo3jR6OeUxDvavuseZ7pPmG3mxCrG/P7klXgHa', 'komeda0703@gmail.com', 0, 12, 0),
('300032', '小林　宏貴', '$2a$10$A5X.d41V4qnc.3O19DbpNe20L0/Nq.1X0gJPs1kZBxKQ8QrzUkQ.i', 'skysora1122kumo@icloud.com', 0, 12, 0),
('300034', '野村　千明', '$2a$10$SLxD4sSeVtzXeJ15x3t69uO/bKnkIp5zJd67sbfFrjiJJQX4v4hnC', 'aim1012.gh@hotmail.co.jp', 0, 5, 0),
('300037', '大久保　龍馬', '$2a$10$LoTDUo.SgKlsB/oLdLs9jeFUtnomgkNcGPq6xzn22NBe8ANB9VIz.', 'qbuy9031@yahoo.co.jp', 0, 10, 0),
('300042', '高橋　祐衣', '$2a$10$fKjmooUR.mlJdnAX47Kl0u/uWrPVXIEW8Nncyg/N3AN8h94Bo6nwK', 'pignon_003@icloud.com', 0, 12, 0),
('300044', '加古　飛来', '$2a$10$pTw5WBH7kRKKpFQQ9/BJYet8dSO6pbOIpLHmE3vRVKKxM9n13t4tG', 'hiraiws@au.com', 0, 12, 0),
('300046', '中田　嵐士', '$2a$10$H2Zpm1Ir7GJ9RV0Fd3HGju8a4x2TCXLZjyPDyl7FXNVr3QHLfg0Lq', 'ruru.arashi0424@gmail.com', 0, 5, 0),
('300049', '髙橋　樹', '$2a$10$Y7LngrSeyepQXuGU.L3YU.m39fogzv96ywr/QYYo69E6u0C.89V6y', 'itsuki.m.k.n.0120@gmail.com', 0, 12, 0),
('400000', '杁本　翔太', '$2a$10$8VFBaPZ7rH0cfgCZ//4DR.9tHqRQGsn23vAwyTP1U6dFHAmMgsltW', 'irimoto0924@icloud.com', 0, 7, 0),
('400001', '高田　青空', '$2a$10$MrDurj95Q.Wwbokjfc5jkO.tY63R.YCqN9WzdVIFdHB0kaNT3SfFe', 'aoao1008@gmail.com', 0, 10, 0),
('400002', '亀田　春樹', '$2a$10$7OYR99gwY/mi/SZGB5QcLugfjiQWV7uTSJ1IZNSJNJ7PyAQbMWdd.', 'haru932004@gmail.com', 0, 10, 0),
('400004', '中島　幸奈', '$2a$10$unxi4TQhuiVZdCnFd.P5DuiLbeYRuw5A4LWyULstrNyQ7n1u2dbvm', 'yukina.tamoto@gmail.com', 0, 10, 0),
('400005', '伊藤　直人', '$2a$10$VORwvuGAYGqBt.TSLOjU/OZ4in/Qbu5StEQgBrkFkgNwj5Isg2c8K', 'sirokara@icloud.com', 0, 12, 0),
('400006', '道脇　侑紀', '$2a$10$i74qHDJm6BSGyk9qTJlgw.QMZzruKTJP0zYJvd9dcZugQ68UEKxge', 'mitiwaki.ojt@gmail.com', 0, 12, 0),
('400012', '舟橋　大裕', '$2a$10$wOO8F2dDFvgbi6.3Ila/n.tGmDgVQDmVOsSKqk4TudSi/K6ZCaliy', 'd.hunahashi@gmail.com', 0, 10, 0),
('400014', '西垣　智仁', '$2a$10$X/ZQ2NTQ4hJE5lbxGzyjeu9BD4n5Q/x1lExDqdzE.NXHM3IVhfLG.', 'a.jam.hieing.koy@gmail.com', 0, 10, 0),
('400017', '山田　悠貴', '$2a$10$qAsN4SJydeHu2VMiwtOtGeyqL8SZI1A7wYzAAZYoUow9sczboSvK6', 'yuuki0430yamada@gmail.com', 0, 12, 0),
('400018', '水谷　政貴', '$2a$10$CwM05zU22r98NnbFsvfxKO.Kq.Ea5RvN7iWNLEXc9/ggTCoNWJnpC', 'm.makun1026@gmail.com', 0, 11, 0),
('400020', '齋藤　拓也', '$2a$10$j5/h/JOS6MDzp2V/XmWi2OAnfr1r20WWIpeae7WV193F6d.z439Lu', 'saitou8118@gmail.com', 0, 12, 0),
('400024', '山下　暁大', '$2a$10$XSVrCPv0HqPJlVi.yZHqc.zIGsyCM7gWxf1d9wd6x9EICzAq0XBZC', 'akatsuki.business1@gmail.com', 0, 10, 0),
('400025', '辻戸　翔希', '$2a$10$N7Uz6iOrE1fyPNMTFfcOJ.kbbLa7yjyyDsnVv3z4cdlRsO.NQ9hKS', '7baske2@gmail.com', 0, 12, 0),
('400029', '新田　遼', '$2a$10$yRmnZVaeCRmWFQjX.HyIUOGhF6UklReBXw7u/ojDdQ631IcaOntSy', 'urutoramandao@yahoo.co.jp', 0, 10, 0),
('400030', '川合　百加', '$2a$10$mxiP8Zdm85EpLu1RvdyLLeDc1F0GZDoPSwNN2v9R70H7LuIGLbZpW', 'hunori_ar@yahoo.co.jp', 0, 19, 0),
('400035', '野口　ショーン', '$2a$10$Tskiby11I7p8O6donVMyHuebLXVEsQsxLH7lcBqQumSrl7gOUVx1u', 'husernamett@gmail.com', 0, 10, 0),
('400036', '鈴木　康文', '$2a$10$poBBwhm61NrDP.nof2rPGelxLv4.UeciPGfYXGMF4wWpuZ7UE5af2',  'kfcs72u6g@i.softbank.jp', 0, 10, 0),
('400040', '熊谷　拓海', '$2a$10$ff8Gx.d1iS0QmGYtZwVvB.5L6yuNxdoHaVhLDM7jqwDQrfYGFIeRu', 'takumi.k1216@gmail.com', 0, 10, 0),
('400041', '馬場　友規', '$2a$10$4mGrP5fjsTav18BvXblGN.d3OjZAkwtv4WLg.hGvyupSb2Z5oIFGW', 'tomo.ans.125@gmail.com', 0, 11, 0),
('400048', '鎌倉　大和', '$2a$10$wExe8tch7u/QDaO5ngSNve4tsduZ4/WUewTpd81LJQIPI/DfT6amq', 'dahel539@gmail.com', 0, 10, 0),
('400051', '小西　涼子', '$2a$10$mCJdOsYz7qBpzJXXjqOjFuEglp22RGBCMoKQR4Fxvl.FW6RcEfvwy', 'ayu.suuuki.r2@gmail.com', 0, 19, 0),
('400056', '宮里　勇希', '$2a$10$AC6fHbNkffClGwnQXofGpedPfae3sLbcwlOYMSfBs1Bcc.lxqj0TK', 'scapiva3498@gmail.com', 0, 10, 0),
('400058', '久保　雅', '$2a$10$Ll/Uajr.ghFU7kKog1MlW./ywbyvBrTG7ZaQ5Vm8pRC3yGJ4o9wiK', 'miyabi.k.131209@gmail.com', 0, 11, 0),
('400062', '武藤　秀平', '$2a$10$lfJQTeJy3J5os0MrzDdROOsCcuwZsAp8jplDvSPArz1yYyrIHQatq', 'shuhei.muto24@gmail.com', 0, 10, 0),
('400063', '滝井　孔明', '$2a$10$O17mdXsAN5HR0/noeZ6x6.xucfHUzk6XZ8TZwtRcd7wHR1MfrNQhy', 'ytakii13@gmail.com', 0, 10, 0),
('400064', '松原　里恵', '$2a$10$AofDIWaVriGvPgum0qe7Q.Dp36/tSm5gI6bmEVhKywp.T8pEB8PwO', 'mtbrre417@gmail.com', 0, 12, 0),
('400065', '宇佐美　修吾', '$2a$10$n5pB6PRuy3JR4/9qU7tuv.4gVReId0wsUmLaf7yUgmv.2UGO/AIBq', 'erihakutomesu1021@ezweb.ne.jp', 0, 12, 0),
('400066', '丸下　直樹', '$2a$10$9WhyLgDzOYqOspkWY/n2A.3XwfWjtmPWHiT9KpDA58VvxyhXW/NwW', 'kyomufire@gmail.com', 0, 10, 0),
('400070', '越山　香穂', '$2a$10$OVlAMQ1rJG0xhwuc1IuDxeIiNMClH3baKky2TA6rza.RV02aburqe', 'alwaysindream1137@gmail.com', 0, 11, 0),
('400072', '西井　理紗', '$2a$10$jPknOX6l.sOUX..0jaEz5uZAKtecqKHCP5708QhStr2dFBjYkCIve', 'yp0409risa@icloud.com', 0, 10, 0),
('400074', '吉村　糸織', '$2a$10$dSjvINiuvmb764UBQ6zcNOwOpYHN99vbgCiWBljWVG9oUn8H57TYC', 'nbkd.lv.novel@gmail.com', 0, 22, 0),
('400075', '古田　栞', '$2a$10$XmrAf/tSbCs.OTWB3wASaOXlUxoQUGfyi6eROin0KJp68lBaJbiSG', 'ppsv.02@gmail.com', 0, 10, 0),
('400077', '柴田　智彬', '$2a$10$sMfM/PJGqFcmyY1VbfaysOGgM3BvgVyISskKKzIN9oaQ5hY10XAJe', 'tomoh11.0223soccer@gmail.com', 0, 10, 0),
('400078', '藤澤　悠貴', '$2a$10$5.jMvT4sBZlNaS0JyTZ9b.u2/xg9TMMkwzOzNwGEgn0iqW7mMzvv6', 'fuji_conan1019@ezweb.ne.jp', 0, 10, 0),
('400083', '禰冝田　心', '$2a$10$urpIjvcd6sVuR/Z21XhDJOndrwZtlldBuoBRg2DewJVTYw2RNf/8O', 'rokoko-1124@ezweb.ne.jp', 0, 11, 0),
('400084', '島田　萌花', '$2a$10$gl7wDtOg20oyojtIPo6sQOVFa7rGqp/zFIRsZ4rVPoeoBUhe3y5UK', 'moe514-1017jms.ciel@au.com', 0, 12, 0),
('400086', '吉川　翔', '$2a$10$wkFMDqiuRf2YO0pQ5QE9LeU98Gd8vqVHnxxQ4g6Yz9qPjJZgpEMve', 'mm.sy1008@gmail.com', 0, 10, 0),
('400087', '櫻庭　由宣', '$2a$10$VJ4q3ikuKMYQBAiwkojZz.xAPcBMhdMKsQII6ZuPkENwZarSCQXG6', 'itssakuraba@gmail.com', 0, 10, 0),
('400088', '北村　弘行', '$2a$10$8Wij02HsmG54A4w5cr1u3uXhGVNbuOlChU3pGP5Otu8EuxgC/X3bi', '0hm31b547233k0g@ezweb.ne.jp', 0, 10, 0),
('400090', '中根　南', '$2a$10$uGynf.mUna9znCh7iWrTputOaGiJFPCbYuDYIEHyZoquSCCwfBQa2', 'nmt0922@gmail.com', 0, 22, 0),
('400093', '小川　留梨奈', '$2a$10$F/99suIDjM7.qodxSjoDjOUXrav46.MOG6a.IstSO0H/lOHdS7YiK', 'rurina.ogawa@gmail.com', 0, 10, 0),
('400095', '木下　英恵', '$2a$10$bW3ZOVwTkWLWB6KVxOl4UODpbIkCW4gIs/v9GB4BACLL64uXxOjh2', 'poa2sharejam@tutamail.com', 0, 11, 0),
('400097', '星野　美里ミラケル', '$2a$10$C9Q8d.qnljM2neC5nRiiR.WlC0KmEDFDndRbJStFFvxf3pUD3W1jO', 'mirakeru14@icloud.com', 0, 12, 0),
('400099', '吉田　梨花', '$2a$10$.WPVRWQOnas9QetpzG1QJ.dWXS8H0yvu7w/lBlcrM3FReE46MDIzi', 'honeyworks1214@icloud.com', 0, 12, 0),
('400101', '甚田　颯一郎', '$2a$10$vZklO8JCb/vKTzHIf1aZ8.aEv0LBYfYRNlzK7Qh49Rdp5uq.QS19G', 'sousoutrombone@icloud.com', 0, 12, 0),
('400102', '柘植　航太', '$2a$10$849w2Zttb4POLzZC.fBSYuJmqwEyszIiwMEalYDlGZvbdMfLnV.uG', 'k.tsuge13@gmail.com', 0, 12, 0),
('400105', '松永　航輝', '$2a$10$/glmoXtChqTcWjUOhoT8oOacgrrinRpRv.tyQCWewDii.9jlI/D9u', 'matu.dora.tama.dora@ezweb.ne.jp', 0, 10, 0),
('400106', '小村　崇斗', '$2a$10$S4wS6tZ3Php4d7QxjuItkOaHi49n/ffJDXBBZu0nxUIdbomXs5ffK', 'komutaka0203@gmail.com', 0, 11, 0),
('400108', '奥本　みどり', '$2a$10$2GDzeabPCk39QuNbhJ3Gx.OnuQERXq6r8BByfpvFk3trlFsQfc0MG', 'mido.8.jeff@gmail.com', 0, 10, 0),
('400109', '佐野　僚希', '$2a$10$YdGKVFx4Ua91zb8Cv2doGOHgEYNRCTToxwHw2HYX2LpZgaImciwk.', 'sano.r.0331@gmail.com', 0, 10, 0),
('400110', '甲元　汐奈', '$2a$10$JWbkxKP3FgCd410HUYuCRuR11EAywlvv62O.Q5MFG.xz3CdhObDk6', 'tanshio1022@icloud.com', 0, 10, 0),
('400112', '今枝　妃夏', '$2a$10$HnqsW1Us7xMEh2CPRYmzI.AHiRls8XAwDUt.21XfZqjxdvLfvww1S', 'sato.h.92921@gmail.com', 0, 12, 0),
('400115', '筒井　一成', '$2a$10$Vk09FwUSfLwUNTPJsQsXLuHw9QH72wclkkqH.E83DqvYaspyTaOq2', 'bakatesuto.samon1997@gmail.com', 0, 10, 0),
('400118', '梯　京生', '$2a$10$naaGN0OTVFwLHo5XhG7Me.AJRqcYLcDLUXkrZHKOoNY.Ql0RSuvT6', 'atsuki.226kk@icloud.com', 0, 11, 0),
('400119', '織田　盛太郎', '$2a$10$6/sZH08hIeYJ6qZZE2p3KubeC4IG.QzVrfx8l/Hv3RVHgTpsHJEEW', 'seitarou.oda@gmail.com', 0, 10, 0),
('400120', '木俣　貴史', '$2a$10$yPDQNSuia1zRtBuLuskjmOQdYCXYyrz3.7idDjvmf/QYZER.B5Dea', 'momoseven-7@outlook.jp', 0, 10, 0),
('400125', '安井　志希', '$2a$10$KEHrF5U40xTeJCZ7VB8uauRIPZjME6CFXtsvG2o20H3YcPW5.1ruu', 'yasui.motoki.1189@gmail.com', 0, 11, 0),
('400127', '足立　実希', '$2a$10$crEhvd3Rm2oCc15i1ZgVs.qQF5SPOIqJh9SycXrV2ZwNdlseImWVC', 'hnrgyu0504@gmail.com', 0, 12, 0),
('400129', '鈴木　笑華', '$2a$10$FLDCnw3BN5h/MUcu0yQemOZq5.lLbW5XxCGDRZfscTbCs9mCkTtsa', 'xiaohualingmu51@gmail.com', 0, 11, 0),
('400130', '田中　聖夏', '$2a$10$E1VpnjUineTUXjrDTOoPW.HmNjZX83RIE1/pr.V85ITp/TVB0hcf2', 'senaliketennis@gmail.com', 0, 11, 0),
('400131', '柘植　清之', '$2a$10$3nIRSz9eaElkeCPiUFdQ2.9T4x2TPUeimOE99Bn9ZZaEsBjU.OPEW', 'kiyoyuki.tsuge@gmail.com', 0, 10, 0),
('400132', '尹　成民', '$2a$10$WNsA7R2CJg/aFZIJJ.jmTep94iQA4QZj9e5wIH0qKFCHjjQWfdgHe', 'mirinae0081@gmail.com', 0, 12, 0),
('400133', '山田　惇司', '$2a$10$vn4.zehOradTMj9yRDxC8.DWRbISm1J2OJm0MSgw9ccwsUJ6U3Nu2', 'soccer.bz178.juice@gmail.com', 0, 10, 0),
('400134', '古川　愛彩', '$2a$10$Qq1cdwdk2X/yKqzj1M7vk.2X/dWOwW9dPXd23eJ9DGex5B3mlMipO', 'fawork1028@gmail.com', 0, 10, 0),
('400135', '長谷川　碧', '$2a$10$ZRIK2FusSBYsKjcVyG9R/.DvYz/Wbi8H83Cz45GwfBk.hB3imNZWK', 'a.hasegawa1119@gmail.com', 0, 10, 0),
('400136', '森　勇希', '$2a$10$Egz3GZwb27SIfctb8iai.ePuZqADenr/T6f5ljJH1h7Kb.tmac1bu', 'yuki.mori42512@gmail.com', 0, 11, 0),
('400138', '中瀬　裕貴', '$2a$10$0Ug8axhAnJmJuSwcvbbZDuMzP6yVNtQWr.I8Kz8fx.zxcYhxBsqxG', 'yukinakase0806@gmail.com', 0, 11, 0),
('400139', '志水　瞭之', '$2a$10$2eNCZVfU5RCz/OHNh5CfPuWO7TV2HK2XBkg4eWDVF5FLDpNKOkM9m', 'shimiryo2408@icloud.com', 0, 12, 0),
('400140', '飯森　ケビン', '$2a$10$vHkHheWgTqNOdW53U1AZ/.AsDTVQVcRpEobbc/AWh4M3PdhuIqy.e', 'kevinimori.gc0402@gmail.com', 0, 10, 0),
('400145', '別所　希輝', '$2a$10$m08PHH8vG8ns7BqJ5LLWqeFPi9gARzMWZY0SDh.qXn76Zu5PJXgfG', 'm.bessho.38b48b@gmail.com', 0, 12, 0),
('400146', '喜多村　綾乃', '$2a$10$i2TlOYsVOMsBxnH52XfLt.Ge075/Cu2LVF.xvkvEibsaLXujFEteq', 'ayano.kitamura06@gmail.com', 0, 11, 0),
('400147', '箕浦　沙也香', '$2a$10$HjnX0NYEGbhF0n0FlZQauem4wu1.I/v/1wM.TNa5NADiy1ONvLSzG', 'emet6201015@gmail.com', 0, 12, 0),
('400148', '四谷　有里', '$2a$10$v1RjGk4h0AILrH/RGO4TeOz81LeVdL4ISGY6Nif2SoI9Aj7Xtfyma', 'liliumauratum428@gmail.com', 0, 10, 0),
('400150', '宇留野　磨宙', '$2a$10$yQd3glh7hKm7RYNP5exX6O6xV7IzLkrQXKRPi64exCWQYFbJnpSoG', 'm.uruno257@gmail.com', 0, 22, 0),
('400154', '岩野　隼土', '$2a$10$ImOW9CrnynvQvxSRmOKVCeiqqsrwFOI9aFquPTEMd1BGnHHgF1STK', 'nt767891@gmail.com', 0, 11, 0),
('400155', '梅村　実夢', '$2a$10$dQUs1F84USWdF3vdPPYoPOSSLjcfFOKuQdSba07GmY6EhqkWACNaW', 'wanima10feet@gmail.com', 0, 10, 0),
('400156', '寺前　咲良', '$2a$10$q4zQbDNwHSEwDKATLxxJxO8Dg9q2HbAELE4S2GqCveJ859jiSs3we', 'xiaoliangsiqian9@gmail.com', 0, 11, 0),
('400157', '徳田　武人', '$2a$10$8u5cBbFDu6nobd6jgnQecOFv6XHgZuYwytxuNOdMiL6BESy0odBdS', 't.tokuda00@gmail.com', 0, 10, 0),
('400158', '中川　悠太郎', '$2a$10$JSIdvxBqB8RPuUYpxuvhBOuahQnjJfiOguGCYdihJDklWtMI.a.sO', 'nanohayate477@gmail.com', 0, 12, 0),
('400159', '永渕　友基', '$2a$10$9df4B5vD7PXO/EWL6Q4wduHLHqMd7FLJvnrMqUJY4eOWkIhd6/vRa', 'nagabuchi.tomoki0222@gmail.com', 0, 11, 0),
('400160', '松本　祐依', '$2a$10$PZhAeT.iAiIKBaovK6UxpeAi3KMu6b4ZwQ.QfRJjqWMVetPcRg1Oa', 'matsumoto.yui312@gmail.com', 0, 10, 0),
('400162', '大塩　あかり', '$2a$10$5TTRJhQ2WZ3X5fx39mVwb.jzGJWv3qS.r4v4wGitGmKYqYxa8CSHC', 'ao.aik79@gmail.com', 0, 12, 0),
('400164', '坂倉　亜依', '$2a$10$BNFjAQQejiSsK790gezmhOWGgukS5dwTMqyFeWRBm7HCcxBkqEP2m', 'aiuver928@gmail.com', 0, 11, 0),
('400165', '彦坂　美怜', '$2a$10$w7Hvh03MbjwbB6uSm9UmseLJ9994oxiiW4J4Ki4ygTPs6HVk0MGMu', 'm_ireeei36@i.softbank.jp', 0, 11, 0),
('400167', '松村　蘭', '$2a$10$UAI0qzQDPk2dl650LHAg7uKPu/ptkUCGXwNlWuxIfRPukUgFmLPIi', 'orchid61v@gmail.com', 0, 11, 0),
('400168', '山口　愛', '$2a$10$VxnPHRyeEILtzsnEsbDJFuI0CWIA/ZOB2Pjkn/KrPSBavA05J6d/m', 'ygimu.work@gmail.com', 0, 10, 0),
('400169', '徳前比嘉　ひかり', '$2a$10$trM/yHbBDm5dPHHTbF8lpeSEn0D1YJA5copE3AAibioRbS0qGe1ri', 'hikapeace9729@gmail.com', 0, 12, 0),
('400170', '望月　晴加', '$2a$10$inIJpFuPOedzn.ZUlMV.2eq5B4s4nmNAivqEvJp3nCdtgXtWrt7Ry', 'haruka2001.2.23@gmail.com', 0, 12, 0),
('400171', '湊原　朱理', '$2a$10$4.36.lH6p7KBWh0Hcp0smupX3u/vLucaVFQQHZF029UeCCJsBGH32', 'minachan.lovesky.103@gmail.com', 0, 12, 0),
('400172', '萩原　楓', '$2a$10$z.4kTtKXL.fntCg2NhmTB.dzEOgC7s5z1Yj0edj7gl52AA7lPZIA2', 'kaedehagihara09@gmail.com', 0, 10, 0),
('400173', '松浦　実祐', '$2a$10$ezyH7.dxCZjT1HQGNga5OOWqH79LzHyN8o/pwRMKlr90WOz2dYG5y', 'k.r.y.s.d-mykp@docomo.ne.jp', 0, 12, 0),
('400174', '佐藤　大地', '$2a$10$dZ8Hnvbq32at8n5j4CobcekPP7ng761EI5RLeswj.fC3x5l26FWVK', 'mk-155_2VOL01@outlook.jp', 0, 10, 0),
('400177', '佐藤　はるか', '$2a$10$h5irIofpkb/P6S6KjwL7wuKdMxX4PskP9T/3CNx34pepd/c3kkx02', 'harukasato11@gmail.com', 0, 11, 0),
('400179', '橋本　理来', '$2a$10$c2B7PxkNGwc9CO1SFj8H7es9mFu3QnWdFu0TDEG7wtGPFDmVpc6IW', 'rikori160324@icloud.com', 0, 11, 0),
('400180', '柴田　茉伊', '$2a$10$n7cGYUuvlEtBzEJ5yAq2y.MJTDa90rKufqCUB2SB70gSQU78luGha', 'maiakitomonana@gmail.com', 0, 10, 0),
('400181', '矢尾　虎太郎', '$2a$10$AIaFwxq8zjGweoj7OQeNmeOUtMN6G.MaG431G1SKYpVimlcBbkY7C', 'sub2828tora@gmail.com', 0, 12, 0),
('400182', '藤本　悠利亜', '$2a$10$uaG5WRo5xJ/eOHgq8rqc1ucGBBcg7.i8rfdFaugU2oMg3oE8E4j3a', 'yurizo1202@icloud.com', 0, 12, 0),
('400183', '清水　麻也人', '$2a$10$GlzRQA/FOum3anwzRdyDf.w6Qyzr5bE1G/mszDo6p0gmBBBrmGaGq', 'mayato_shimizu98@icloud.com', 0, 10, 0),
('400184', '松下　琴音', '$2a$10$EYaI/WnLEaCm1PvP5KXptOC0/7ExXXXyeS2f/kGsfhgyMUcsl26Ry', 'kotonomu0607@gmail.com', 0, 10, 0),
('400185', '小暮　茉咲', '$2a$10$R4I9AO2PuRIhPBgQpWxAEunB6uO66Gj/JFfIWf/0iQic/xfM5WFQ6', 'masaki020730@gmail.com', 0, 12, 0),
('400186', '山下　雄大', '$2a$10$wDSHMQpE.564PrOzl0.KvOxRRSmmyYK2iMjRJVYcoHXfK5W0HwXGe', 'yptkyyp57575@gmail.com', 0, 10, 0),
('400187', '津田　昂汰', '$2a$10$.Ps.PY1VY1SU9lJs3G4Tfe3ChH9/LiLOyC8u0X2rUNaFQV98djJk6', 'KTsuda172770@gmail.com', 0, 10, 0),
('400188', '松原　稜雅', '$2a$10$VQz2sA0veSAvjganKt5Xnu14x9.9LQc9uvm.Ew35dnfjp4PPEEIFC', 'romikano81@gmail.com', 0, 12, 0),
('400189', '山川　貴也', '$2a$10$WPKQLuQbdtQzx9TEJ4dVdeHD6MIseauDikFUeALGXJ92GDc.Gutye', 'tkyeah.kwsk.0106@gmail.com', 0, 11, 0),
('400190', '福田　健史', '$2a$10$4HOfsfC5ykvpX9ZV711a.uJU7VwpLblvpEG1AD45W6lJofy9CpqZW', 'ke0519nshi@yahoo.co.jp', 0, 12, 0),
('400191', '嘉陽　桃夏', '$2a$10$maR550W8oaTACixtarNGp.5xiafvw3XjnCVueVzadPz55Y11lg1P6', 'kayoumomoka@gmail.com', 0, 12, 0),
('400192', '居川　陽菜子', '$2a$10$KZIX.xoWw8oJNKlJ2WamDOIEpSyJBNDi4gPcVUGyTXthFPZE0VQf2', 'th.aekh.luv@gmail.com', 0, 12, 0),
('400193', '吉田　純奈', '$2a$10$UibHqZBBlImcg5xuiEQElu1k6qRrZbFoc4v9BIUKElh2xV/Ag5emy', 'under.the.sea8.222@gmail.com', 0, 12, 0),
('400194', '庄　麗羅', '$2a$10$AE7hBI8pdKZOCU.W8s5fB.XK2IjjiyzWWnL3Yeoiv1uZZ9CHIedme', 'jump_urara-0409_love@docomo.ne.jp', 0, 12, 0),
('400195', '中洞　慶人', '$2a$10$rSBcAZkN3.NehJim8s1zbO8zepKD6p1WtgRrnT/nqc1B68MuzsLMu', 'nkbrkit@gmail.com', 0, 12, 0),
('400196', '宮園　翔太', '$2a$10$PXdxRgIT0DECpAtggqzCiO0u8f8JWp3u4/QyxkP4byV8mSmUdhILG', 'ii9312470@gmail.com', 0, 12, 0),
('400197', '青木　彩花', '$2a$10$QxQ4s5ezbSclPSs.CvOac.1PgkBbriM8Wu0UzYGfKoBshJ2WVIX5y', 'aya1115.nt@gmail.com', 0, 10, 0),
('400199', '山口　俊輔', '$2a$10$gELldSuoFsYZCpMN1zg8ru292Qn5t6mv2UjHWvx9p0xWq96qS./wi', 'guchi633290@gmail.com', 0, 12, 0),
('400200', '伊藤　丈留', '$2a$10$pRMPKhcmFx5hsV7FwZP1JOXr70Ntg0ihc8hHqQr/SVPhBJuAB5tgq', 'takeru0826.g@gmail.com', 0, 12, 0),
('400201', '喜井　理規', '$2a$10$.4GWHaDr75oy6wfPmq0.F.9eaXccLupT/6aj/YjGziNOn5OO5wydO', 'milkteahxh4510@gmail.com', 0, 12, 0),
('400202', '小池　拓望', '$2a$10$RRVV40tJ.Aa3Zc7VDFQcsuiuw.QRIkbPtd4f4RlS.6wp0PfEBuUPK', 't.koike070217@gmail.com', 0, 12, 0),
('400203', '長谷川　隆司', '$2a$10$RDxWEW1wr1Z72cUVdqdTaON8UekzXGNUgMt1mD.II5/XALiCj9kJ2', 'kiri893@gmail.com', 0, 10, 0),
('400204', '田口　真衣', '$2a$10$yZGbdAyfw0DkY3aXAwREXuICS2AzDomSKL1.PaMF.4Lf180xJ.JUa', 'tiankouzhenyi3@gmail.com', 0, 12, 0),
('400205', '松浦　未来', '$2a$10$5TWezGsjJ8af/msXE0jr5.9uuGDzwsn9unrBFJa6YJn5R2RCroZzi', 'sbrsrrstsy8@gmail.com', 0, 11, 0),
('400206', '半谷　竜義', '$2a$10$9l/HIAD.L/zv05TjyTndTeidSsrqQbzJzg11KiQy9w32JPTw1nReK', 'hanya.tatsuyoshi@gmail.com', 0, 11, 0),
('400207', '山脇　卓冶', '$2a$10$iFP4FUQ5hjXfvnqLTM0WQOZxnopvRIV3AgpN0.hqIHxH0CRg1D6e.', 'am.jagteem264@gmail.com', 0, 10, 0),
('400208', '榎土　桃香', '$2a$10$al8js3jOEZND6S3pg12td.t6E2PjiRUt5ypLKykYIsmnoREPFhDci', 'king04011129@gmail.com', 0, 10, 0),
('400209', '竹内　菜々花', '$2a$10$BZfz/XFu.DCB9v8TfzcWh.2zCoC.2eDIWrhc8HtgyWV3uHoVe1s7m', 'nana.takepost@gmail.com', 0, 12, 0),
('400210', '虎澤　柚奈', '$2a$10$R94yQT7zPnPMn8un3AXkfuQ5aA1.p/KpACZjjSx1WQGDDoxSwUUZe', '84y.torazawa@gmail.com', 0, 11, 0),
('410001', '徳田　海', '$2a$10$7uCTqhsmMb2wmnMUvxrLzuUwnHMmsbR37TgRfj0LASsm4CKGKGEa6', 'seabullet@icloud.com', 0, 7, 0),
('4002122','admin','$2a$10$QNW1k/8Ngi8cX0i6Zel2HOwFVZPmW9Qn1SGizlEizKzKIkOIzO/Tq','admin@gmail.com',1,3,0);
/*!40000 ALTER TABLE `t_user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-05 21:23:15
