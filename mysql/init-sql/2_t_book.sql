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
-- Table structure for table `t_book`
--

DROP TABLE IF EXISTS `t_book`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_book` (
  `book_id` int NOT NULL,
  `title` varchar(255) NOT NULL,
  `categories` varchar(255) NOT NULL,
  `borrower_id` int DEFAULT NULL,
  `provider_id` int DEFAULT NULL,
  `provider_comment` varchar(255) DEFAULT NULL,
  `memo` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_book`
--

LOCK TABLES `t_book` WRITE;
/*!40000 ALTER TABLE `t_book` DISABLE KEYS */;
INSERT INTO `t_book` VALUES
(10090001,'1週間でCCNAの基礎が学べる 第3版','試験参考書',null,null,null,null),
(10090002,'CCNA完全合格テキスト＆問題集','試験参考書',null,null,null,null),
(10090003,'Cisco CCNA問題集','試験参考書',null,null,null,null),
(10090004,'出るとこだけ！ 基本情報技術者 科目Ｂ 第4版','試験参考書',null,null,null,null),
(10090005,'出るとこだけ！ 基本情報技術者 科目Ｂ 第3版','試験参考書',null,null,null,null),
(10090006,'出るとこだけ！ 基本情報技術者 テキスト&問題集 2022年版','試験参考書',null,null,null,null),
(10030001,'詳細! Python3 入門ノート','Python',null,null,null,null),
(99990001,'動車のしくみ パーフェクト辞典','その他',null,null,null,null),
(10090007,'GUGA公認 生成ＡＩパスポート テキスト&問題集 改訂版','試験参考書',null,null,null,null),
(10090008,'かんたん合格 ITパスポート教科書 令和2年度','試験参考書',null,null,null,null),
(10090009,'かんたん合格 ITパスポート教科書 2019年度','試験参考書',null,null,null,null),
(10020001,'Cの絵本 Cの言語が好きになる新しい9つの扉','C言語',null,null,null,null),
(10130001,'ChatGPT完全ガイド 最強のAI活用術','IT・テクノロジー一般',null,null,null,null),
(10150001,'これだけは知っておきたい派遣元責任者に必要な基礎知識','試験参考書',null,null,null,null),
(10090010,'よくわかるマスター MOS Excel365&2019 Expert 対策テキスト&問','試験参考書',null,null,null,null),
(10090011,'C言語プログラミング能力認定試験 2級 過去問題集','試験参考書',null,null,null,null),
(10090012,'ITパスポート パーフェクトラーニング 過去問題集','試験参考書',null,null,null,null),
(99990002,'エクセル&ワード&パワポ&エクセル関数','その他',null,null,null,null),
(10090013,'いちばんやさしい ITパスポート 絶対合格の教科書','試験参考書',null,null,null,null),
(10090014,'いちばんやさしい 基本情報技術者 絶対合格の教科書','試験参考書',null,null,null,null),
(10090015,'この1冊で合格! 丸山紀代のITパスポート テキスト&問題集','試験参考書',null,null,null,null),
(10140001,'20代で身につけるべき「本当の教養」を教えよう。','ビジネス',null,null,null,null),
(10130002,'かんたんUML入門','IT・テクノロジー一般',null,null,null,null),
(10040001,'2EXCEL VBA 初心者のための集中講座','VBA',null,null,null,null),
(10130003,'Iオラクルマスター教科書 Silver SQL','IT・テクノロジー一般',null,null,null,null),
(10020002,'C言語 ゼロからはじめるプログラミング','C言語',null,null,null,null),
(10020003,'新・明解 C言語入門編 6色版','C言語',null,null,null,null),
(10130004,'最新版 Ubuntu24.04 LTS 徹底解説/70ステップでLinuxをマスター','IT・テクノロジー一般',null,null,null,null),
(10130005,'2LEDからセンサー、モーターまでラズパイ工作 パーツ大全700','IT・テクノロジー一般',null,null,null,null),
(10020004,'C言語ではじめる Raspberry Pi 徹底入門','C言語',null,null,null,null),
(10130006,'キタミ式 基本情報技術者','IT・テクノロジー一般',null,null,null,null),
(10090016,'Iかんたん合格　基本上表技術者教科書','試験参考書',null,null,null,null),
(10090017,'JavaプログラマSilver SE11','試験参考書',null,null,null,null),
(10130007,'ITの仕事に就いたら「最低限」知っておきたい最新の常識','IT・テクノロジー一般',null,null,null,null),
(10140002,'結果を出すﾘｰﾀﾞｰほどこだわらない','ビジネス',null,null,null,null),
(10010001,'スッキリわかるJava入門','Java',null,null,null,null),
(10090018,'Java Silver SE11 問題集[1Z0-815対応]','試験参考書',null,null,null,null),
(10090019,'Java Silver SE11 問題集[1Z0-815対応]','試験参考書',null,null,null,null),
(10090020,'Java Silver SE11 問題集[1Z0-815対応]','試験参考書',null,null,null,null),
(10090021,'Java Silver SE11 問題集[1Z0-815対応]','試験参考書',null,null,null,null),
(10090022,'Java Bronze SE 問題集[1Z0-818対応]','試験参考書',null,null,null,null),
(10090023,'Java Bronze SE 問題集[1Z0-818対応]','試験参考書',null,null,null,null),
(10130008,'プログラマー脳 ~優れたプログラマーになるための認知科学に基づくアプローチ','IT・テクノロジー一般',null,null,null,null),
(10160001,'最新マーケティングの教科書 2021','マーケティング',null,null,null,null),
(10130009,'この一冊でソフトウェアテストの基本がわかる!','IT・テクノロジー一般',null,null,null,null),
(10140003,'3秒で相手の性格を見抜く方法','ビジネス',null,null,null,null),
(10140004,'メタバース革命　バーチャル経済圏のつくり方','ビジネス',null,null,null,null),
(10010002,'スッキリわかる サーブレット&JSP 入門','Java',null,null,null,null),
(10010003,'スッキリわかるJava入門','Java',null,null,null,null),
(10250001,'イチロー　インタビューズ　激闘の軌跡 2000-2019','スポーツ',null,null,null,null),
(10200001,'やる気のスイッチ','自己啓発',null,null,null,null),
(10130010,'5Gビジネス見るだけノート','IT・テクノロジー一般',null,null,null,null),
(10030002,'Head First Python','Python',null,null,null,null),
(10230001,'これ1冊でできる！ ラズベリー・パイ超入門','Raspberry Pi',null,null,null,null),
(10130011,'これからWebをはじめる人のHTML&CSS,JavaScriptのきほんのきほん','IT・テクノロジー一般',null,null,null,null),
(10030003,'文系でも転職・副業で稼げる AIプログラミングが最速で学べる！','Python',null,null,null,null),
(10240001,'お金のいらいない国4 ~学校は？教育は？～','文学',null,null,null,null),
(10240002,'お金のいらいない国2 ~結婚って？家族って？～','文学',null,null,null,null),
(10240003,'お金のいらいない国','文学',null,null,null,null),
(10170001,'Jﾍﾏな奴ほど名を残す　エラーと間違いの人類史','歴史',null,null,null,null),
(10260001,'株式投資 長期投資で成功するための完全ガイド','経済',null,null,null,null),
(10180001,'36年後のニッポンを知れば2014年がわかる2050年の日本列島大予測','社会',null,null,null,null),
(99990003,'産業廃棄物又は特別管理産業廃棄物処理業の許可申請に関する講習会テキスト2022年度　資料集','その他',null,null,null,null),
(99990004,'東海　肉の店　2021','その他',null,null,null,null),
(99990005,'産業廃棄物又は特別管理産業廃棄物処理業の許可申請に関する講習会テキスト2022年度　共通/収集・運搬','その他',null,null,null,null),
(99990006,'これだけは知っておきたい　派遣元責任者に必要な基礎知識　派遣元責任者講習テキスト','その他',null,null,null,null),
(10270001,'2015年版　今がわかる　時代がわかる世界地図','地理',null,null,null,null),
(99990007,'令和2年度　労働者派遣事業及び請負事適正化のためのガイドブック','その他',null,null,null,null),
(99990008,'職業紹介責任者の手引き','その他',null,null,null,null),
(10140005,'超図解　最少の時間と労力で最大の成果を出す「仕組み」仕事術','ビジネス',null,null,null,null),
(10260002,'会社四季報　業界地図　2015年版','経済',null,null,null,null),
(99990009,'2015年改正　労働者派遣法','その他',null,null,null,null),
(10280001,'【最新版】世界権力者　人物図鑑','政治',null,null,null,null),
(10090024,'簿記3級講座','試験参考書',null,null,null,null),
(10290001,'何度も読みたい広告コピー','デザイン',null,null,null,null),
(10220001,'超使える英会話表現1110','語学',null,null,null,null),
(10090025,'きほんを学ぶ世界遺産100 世界遺産検定3級公式テキスト','試験参考書',null,null,null,null),
(10090026,'世界遺産検定公式過去問題集　3・4級　2017年度版','試験参考書',null,null,null,null),
(10140006,'あたなのスピーチレベルかあなたの年収を決めている','ビジネス',null,null,null,null),
(10300001,'開放区','エッセイ',null,null,null,null),
(10210001,'幸せな小金持ちへの8つのステップ','ライフハック',null,null,null,null),
(10220002,'英語はインド式で学べ！','語学',null,null,null,null),
(10150002,'偉人の選択100 STEVE JOBS','経営',null,null,null,null),
(10220003,'世界中どこでも通じる　すぐに使える英会話(ミニフレーズ2500)','語学',null,null,null,null),
(10140007,'MONEY','ビジネス',null,null,null,null),
(10140008,'「心理戦」で絶対に負けない本　実戦編','ビジネス',null,null,null,null),
(10200002,'世界一やさしい成功法則の本','自己啓発',null,null,null,null),
(10200003,'20代にしておきたい17のこと','自己啓発',null,null,null,null),
(10200004,'20代にしておきたい17のこと','自己啓発',null,null,null,null),
(10300002,'決めて断つ　ぶれないために大切なこと','エッセイ',null,null,null,null),
(10170002,'侵略の世界史　この500年、白人は世界で何をしてきたか','歴史',null,null,null,null),
(10310001,'必ず誰かに話したくなる心理学99題！','心理学',null,null,null,null),
(10200005,'ORIGINALS誰もが「人と違うこと」ができる時代','自己啓発',null,null,null,null),
(99990010,'楽観主義者の未来予測　テクノロジーの爆発的進化が世界を豊かにする　上','その他',null,null,null,null),
(99990011,'楽観主義者の未来予測　テクノロジーの爆発的進化が世界を豊かにする　下','その他',null,null,null,null),
(10210002,'なぜ、人は動かされるのか　影響力の武器　[第三版]','ライフハック',null,null,null,null),
(10200006,'自分の中に毒を持て　あなたは”常識人間”を捨てられるか','自己啓発',null,null,null,null),
(10140009,'99%の人がしていない たった1%の仕事のコツ','ビジネス',null,null,null,null),
(10140010,'1億円稼ぐ空売りの極意','ビジネス',null,null,null,null),
(10220004,'Basic2400 ver.2','語学',null,null,null,null),
(10210003,'ネットがつながらなかったので仕方なく本を1000冊読んで考えた⇒そしたら意外に役立った','ライフハック',null,null,null,null),
(10140011,'17歳のための世界と日本の見方 セイゴオ先生の人間文化講義','ビジネス',null,null,null,null),
(10140012,'人生の勝算','ビジネス',null,null,null,null),
(10160002,'誰かに教えたくなる世界一流企業のキャッチフレーズ','マーケティング',null,null,null,null),
(10300003,'Adventure Life','エッセイ',null,null,null,null),
(10200007,'天使にもらった贈りもの 潜在意識で自分を変える、思いを叶える','自己啓発',null,null,null,null),
(10300004,'愛しあおう。旅にでよう。','エッセイ',null,null,null,null),
(10300005,'LOVE&FREE 世界の路上に落ちていた言葉','エッセイ',null,null,null,null),
(10200008,'さあ、才能に目覚めよう','自己啓発',null,null,null,null),
(10200009,'スタンフォードの自分を変える教室','自己啓発',null,null,null,null),
(10140013,'創造と変革の志士たちへ','ビジネス',null,null,null,null),
(99990012,'未来につながる、ともに歩く','その他',null,null,null,null),
(10280002,'世界の富の99%はハプスブルク家と英国王室が握っている','政治',null,null,null,null),
(10260003,'2025年東京不動産大暴落','経済',null,null,null,null),
(10200010,'自分の時間　1日24時間でどう生きるか','自己啓発',null,null,null,null),
(10180002,'GLOBAL TRENDS 2030　2030年世界はこう変わる　アメリカ情報機関が分析した「17年後の未来」','社会',null,null,null,null),
(10320001,'超訳　ニーチェの言葉','哲学',null,null,null,null),
(10140014,'楽天流','ビジネス',null,null,null,null),
(10140015,'楽天流','ビジネス',null,null,null,null),
(99990013,'通訳日記　ザックジャパン1397日の記録','その他',null,null,null,null),
(10300006,'W〜ダブル〜　人とは違う、それでもいい','その他',null,null,null,null),
(10200011,'一瞬で残りの97%の潜在能力を引き出す方法','自己啓発',null,null,null,null),
(10300007,'いつもココロに青空を。青空はつながっている。','エッセイ',null,null,null,null),
(10160003,'世界40億人を優良顧客にする！ほんとうの金融を求めて創った仕組み','マーケティング',null,null,null,null),
(10180003,'100年予測　THE NEXT 100 YEARS A FORECAST, FOR THE 21ST CENTURY 世界最強のインテリジェンス企業が示す未来覇権地図','社会',null,null,null,null),
(99990014,'一生かかっても知り得ない　年収1億円思考','その他',null,null,null,null),
(99990015,'ブチ抜く力','その他',null,null,null,null),
(10140016,'amazon 世界最先端の戦略がわかる','ビジネス',null,null,null,null),
(10300008,'天／音。','エッセイ',null,null,null,null),
(10150003,'SoftBank ソフトバンク新30年ビジョン','経営',null,null,null,null),
(10150004,'1000人の経営者を救ってきたコンサルタントが教える社長のお金の基本','経営',null,null,null,null),
(99990016,'日本の戦後を知るための12人','その他',null,null,null,null),
(10330001,'フリーターから資産家になった男が教える億の富の作り方','金融',null,null,null,null),
(99990017,'EXILE　夢の向こうの志','その他',null,null,null,null),
(99990018,'破天荒フェニックス','その他',null,null,null,null),
(10150005,'愛される企業','経営',null,null,null,null),
(10330002,'株式vs不動産　投資するならどっち？','金融',null,null,null,null),
(99990019,'天才','その他',null,null,null,null),
(10140017,'文化資本の経営','ビジネス',null,null,null,null),
(10200012,'非常識な成功法則','自己啓発',null,null,null,null),
(10140018,'4大メガテックの儲けのしくみが2時間でわかる！　GAFA 見るだけノート','ビジネス',null,null,null,null),
(10140019,'DESIGN A BETTER BUSINESS ビジネスイノベーション実践のためのツール、スキル、マインドセット','ビジネス',null,null,null,null),
(10140020,'WHO YOU ARE','ビジネス',null,null,null,null),
(10340001,'ザ・コピーライティング　心の琴線にふれる言葉の法則','ライティング',null,null,null,null),
(10200013,'100% すべての夢を叶えてくれる・・・たったひとつの原則','自己啓発',null,null,null,null),
(10200014,'仕事の説明書〜あなたは今どんなゲームをしているのか〜','自己啓発',null,null,null,null),
(10320002,'吉田松陰　武教全書講錄　全积注 川口雅昭','哲学',null,null,null,null),
(10300009,'キズナ','エッセイ',null,null,null,null),
(99990020,'FREEDOM','その他',null,null,null,null),
(10300010,'ビビリ','エッセイ',null,null,null,null),
(10080001,'Excel 困った！＆便利技339','Excel',null,null,null,null),
(10080002,'Excel 関数の基本と便利がこれ1冊でわかる本','Excel',null,null,null,null),
(10200015,'人生の地図','自己啓発',null,null,null,null);

/*!40000 ALTER TABLE `t_book` ENABLE KEYS */;
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
