package bg.sofia.uni.fmi.mjt.space;

import bg.sofia.uni.fmi.mjt.space.exception.CipherException;
import bg.sofia.uni.fmi.mjt.space.exception.TimeFrameMismatchException;
import bg.sofia.uni.fmi.mjt.space.mission.Mission;
import bg.sofia.uni.fmi.mjt.space.mission.MissionStatus;
import bg.sofia.uni.fmi.mjt.space.rocket.Rocket;
import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class MJTSpaceScannerTest {
    private static String missionsDataSet;
    private static String rocketDataSet;

    private static SecretKey secretKey;

    private static MJTSpaceScanner mjtSpaceScanner;

    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final int KEY_SIZE_IN_BITS = 128;

    private static final int SKIP_LEGEND_LINE = 1;
    private static final int GET_COUNTRY_ARG = 1;
    private static final int INVALID_ARG = -1;
    private static final int TOP_FIVE = 5;
    private static final int TOP_TEN = 10;
    private static final int TOP_TWENTY = 20;

    @BeforeAll
    static void setUpDatasetForMissionsAndRockets() {
        // mission will be created from this data set
        missionsDataSet =
                """
                        Unnamed: 0,Company Name,Location,Datum,Detail,Status Rocket," Rocket",Status Mission
                        0,SpaceX,"LC-39A, Kennedy Space Center, Florida, USA","Fri Aug 07, 2020",Falcon 9 Block 5 | Starlink V1 L9 & BlackSky,StatusActive,"50.0 ",Success
                        1,CASC,"Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China","Thu Aug 06, 2020",Long March 2D | Gaofen-9 04 & Q-SAT,StatusActive,"29.75 ",Success
                        2,SpaceX,"Pad A, Boca Chica, Texas, USA","Tue Aug 04, 2020",Starship Prototype | 150 Meter Hop,StatusActive,,Success
                        3,Roscosmos,"Site 200/39, Baikonur Cosmodrome, Kazakhstan","Thu Jul 30, 2020",Proton-M/Briz-M | Ekspress-80 & Ekspress-103,StatusActive,"65.0 ",Success
                        4,ULA,"SLC-41, Cape Canaveral AFS, Florida, USA","Thu Jul 30, 2020",Atlas V 541 | Perseverance,StatusActive,"145.0 ",Success
                        5,CASC,"LC-9, Taiyuan Satellite Launch Center, China","Sat Jul 25, 2020","Long March 4B | Ziyuan-3 03, Apocalypse-10 & NJU-HKU 1",StatusActive,"64.68 ",Success
                        6,Roscosmos,"Site 31/6, Baikonur Cosmodrome, Kazakhstan","Thu Jul 23, 2020",Soyuz 2.1a | Progress MS-15,StatusActive,"48.5 ",Success
                        7,CASC,"LC-101, Wenchang Satellite Launch Center, China","Thu Jul 23, 2020",Long March 5 | Tianwen-1,StatusActive,,Success
                        8,SpaceX,"SLC-40, Cape Canaveral AFS, Florida, USA","Mon Jul 20, 2020",Falcon 9 Block 5 | ANASIS-II,StatusActive,"50.0 ",Success
                        9,JAXA,"LA-Y1, Tanegashima Space Center, Japan","Sun Jul 19, 2020",H-IIA 202 | Hope Mars Mission,StatusActive,"90.0 ",Success
                        10,Northrop,"LP-0B, Wallops Flight Facility, Virginia, USA","Wed Jul 15, 2020",Minotaur IV | NROL-129,StatusActive,"46.0 ",Success
                        11,ExPace,"Site 95, Jiuquan Satellite Launch Center, China","Fri Jul 10, 2020","Kuaizhou 11 | Jilin-1 02E, CentiSpace-1 S2",StatusActive,"28.3 ",Failure
                        12,CASC,"LC-3, Xichang Satellite Launch Center, China","Thu Jul 09, 2020",Long March 3B/E | Apstar-6D,StatusActive,"29.15 ",Success
                        13,IAI,"Pad 1, Palmachim Airbase, Israel","Mon Jul 06, 2020",Shavit-2 | Ofek-16,StatusActive,,Success
                        14,CASC,"Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China","Sat Jul 04, 2020",Long March 2D | Shiyan-6 02,StatusActive,"29.75 ",Success
                        15,Rocket Lab,"Rocket Lab LC-1A, M?\u0081hia Peninsula, New Zealand","Sat Jul 04, 2020",Electron/Curie | Pics Or It Didn??¦t Happen,StatusActive,"7.5 ",Failure
                        16,CASC,"LC-9, Taiyuan Satellite Launch Center, China","Fri Jul 03, 2020",Long March 4B | Gaofen Duomo & BY-02,StatusActive,"64.68 ",Success
                        17,SpaceX,"SLC-40, Cape Canaveral AFS, Florida, USA","Tue Jun 30, 2020",Falcon 9 Block 5 | GPS III SV03,StatusActive,"50.0 ",Success
                        18,CASC,"LC-2, Xichang Satellite Launch Center, China","Tue Jun 23, 2020",Long March 3B/E | Beidou-3 G3,StatusActive,"29.15 ",Success
                        19,CASC,"Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China","Wed Jun 17, 2020","Long March 2D | Gaofen-9 03, Pixing III A & HEAD-5",StatusActive,"29.75 ",Success
                        20,SpaceX,"SLC-40, Cape Canaveral AFS, Florida, USA","Sat Jun 13, 2020",Falcon 9 Block 5 | Starlink V1 L8 & SkySat 16 to 18,StatusActive,"50.0 ",Success
                        21,Rocket Lab,"Rocket Lab LC-1A, M?\u0081hia Peninsula, New Zealand","Sat Jun 13, 2020",Electron/Curie | Don't stop me now!,StatusActive,"7.5 ",Success
                        22,CASC,"LC-9, Taiyuan Satellite Launch Center, China","Wed Jun 10, 2020",Long March 2C | Haiyang-1D,StatusActive,"30.8 ",Success
                        23,SpaceX,"SLC-40, Cape Canaveral AFS, Florida, USA","Thu Jun 04, 2020",Falcon 9 Block 5 | Starlink V1 L7,StatusActive,"50.0 ",Success
                        24,CASC,"Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China","Sun May 31, 2020",Long March 2D | Gaofen-9-02 & HEAD-4,StatusActive,"29.75 ",Success
                        25,SpaceX,"LC-39A, Kennedy Space Center, Florida, USA","Sat May 30, 2020",Falcon 9 Block 5 | SpaceX Demo-2,StatusActive,"50.0 ",Success
                        26,CASC,"Xichang Satellite Launch Center, China","Fri May 29, 2020",Long March 11 | XJS-G and XJS-H,StatusActive,"5.3 ",Success
                        27,Virgin Orbit,"Cosmic Girl, Mojave Air and Space Port, California, USA","Mon May 25, 2020",LauncherOne | Demo Flight,StatusActive,"12.0 ",Failure
                        28,VKS RF,"Site 43/4, Plesetsk Cosmodrome, Russia","Fri May 22, 2020",Soyuz 2.1b/Fregat-M | Cosmos 2546,StatusActive,,Success
                        29,MHI,"LA-Y2, Tanegashima Space Center, Japan","Wed May 20, 2020",H-IIB | HTV-9,StatusRetired,"112.5 ",Success
                        30,ULA,"SLC-41, Cape Canaveral AFS, Florida, USA","Sun May 17, 2020",Atlas V 501 | OTV-6 (USSF-7),StatusActive,"120.0 ",Success
                        31,ExPace,"Site 95, Jiuquan Satellite Launch Center, China","Tue May 12, 2020",Kuaizhou 1A | Xingyun-2 01 (Wuhan) & 02,StatusActive,,Success
                        32,CASC,"LC-101, Wenchang Satellite Launch Center, China","Tue May 05, 2020",Long March 5B | Test Flight (New Crew Capsule),StatusActive,,Success
                        33,Roscosmos,"Site 31/6, Baikonur Cosmodrome, Kazakhstan","Sat Apr 25, 2020",Soyuz 2.1a | Progress MS-14,StatusActive,"48.5 ",Success
                        34,SpaceX,"LC-39A, Kennedy Space Center, Florida, USA","Wed Apr 22, 2020",Falcon 9 Block 5 | Starlink V1 L6,StatusActive,"50.0 ",Success
                        35,IRGC,"Launch Plateform, Shahrud Missile Test Site","Wed Apr 22, 2020",Qased | Noor 1,StatusActive,,Success
                        36,CASC,"LC-2, Xichang Satellite Launch Center, China","Thu Apr 09, 2020",Long March 3B/E | Nusantara Dua,StatusActive,"29.15 ",Failure
                        37,Roscosmos,"Site 31/6, Baikonur Cosmodrome, Kazakhstan","Thu Apr 09, 2020",Soyuz 2.1a | Soyuz MS-16,StatusActive,"48.5 ",Success
                        38,ULA,"SLC-41, Cape Canaveral AFS, Florida, USA","Thu Mar 26, 2020",Atlas V 551 | AEHF 6,StatusActive,"153.0 ",Success
                        39,CASC,"LC-3, Xichang Satellite Launch Center, China","Tue Mar 24, 2020",Long March 2C | Yaogan-30-06,StatusActive,"30.8 ",Success
                        40,Arianespace,"Site 31/6, Baikonur Cosmodrome, Kazakhstan","Sat Mar 21, 2020",Soyuz 2.1b/Fregat | OneWeb #3,StatusActive,"48.5 ",Success
                        41,SpaceX,"LC-39A, Kennedy Space Center, Florida, USA","Wed Mar 18, 2020",Falcon 9 Block 5 | Starlink V1 L5,StatusActive,"50.0 ",Success
                        42,VKS RF,"Site 43/4, Plesetsk Cosmodrome, Russia","Mon Mar 16, 2020",Soyuz 2.1b/Fregat-M | Cosmos 2545,StatusActive,,Success
                        43,CASC,"LC-201, Wenchang Satellite Launch Center, China","Mon Mar 16, 2020",Long March 7A | XJY-6,StatusActive,,Failure
                        44,CASC,"LC-2, Xichang Satellite Launch Center, China","Mon Mar 09, 2020",Long March 3B/E | Beidou-3 G2,StatusActive,"29.15 ",Success
                        45,SpaceX,"SLC-40, Cape Canaveral AFS, Florida, USA","Sat Mar 07, 2020",Falcon 9 Block 5 | CRS-20,StatusActive,"50.0 ",Success
                        46,VKS RF,"Site 43/3, Plesetsk Cosmodrome, Russia","Thu Feb 20, 2020",Soyuz 2.1a/Fregat-M | Meridian-M n†\u00AD19L,StatusActive,"48.5 ",Success
                        47,CASC,"LC-3, Xichang Satellite Launch Center, China","Wed Feb 19, 2020",Long March 2D | XJS-C to F,StatusActive,"29.75 ",Success
                        48,Arianespace,"ELA-3, Guiana Space Centre, French Guiana, France","Tue Feb 18, 2020",Ariane 5 ECA | JCSAT-17 & GEO-KOMPSAT 2B,StatusActive,"200.0 ",Success
                        49,SpaceX,"SLC-40, Cape Canaveral AFS, Florida, USA","Mon Feb 17, 2020",Falcon 9 Block 5 | Starlink V1 L4,StatusActive,"50.0 ",Success
                        50,Northrop,"LP-0A, Wallops Flight Facility, Virginia, USA","Sat Feb 15, 2020",Antares 230+ | CRS NG-13,StatusActive,"85.0 ",Success
                        51,ULA,"SLC-41, Cape Canaveral AFS, Florida, USA","Mon Feb 10, 2020",Atlas V 411 | Solar Orbiter,StatusActive,"115.0 ",Success
                        52,ISA,"Imam Khomeini Spaceport, Semnan Space Center, Iran","Sun Feb 09, 2020",Simorgh | Zafar 1,StatusActive,,Failure
                        53,MHI,"LA-Y1, Tanegashima Space Center, Japan","Sun Feb 09, 2020",H-IIA 202 | IGS-Optical 7,StatusActive,"90.0 ",Success
                        54,Arianespace,"Site 31/6, Baikonur Cosmodrome, Kazakhstan","Thu Feb 06, 2020",Soyuz 2.1b/Fregat | OneWeb #2,StatusActive,"48.5 ",Success
                        55,Rocket Lab,"Rocket Lab LC-1A, M?\u0081hia Peninsula, New Zealand","Fri Jan 31, 2020",Electron/Curie | Birds of a Feather / NROL-151,StatusActive,"7.5 ",Success
                        56,SpaceX,"SLC-40, Cape Canaveral AFS, Florida, USA","Wed Jan 29, 2020",Falcon 9 Block 5 | Starlink V1 L3,StatusActive,"50.0 ",Success
                        57,SpaceX,"LC-39A, Kennedy Space Center, Florida, USA","Sun Jan 19, 2020",Falcon 9 Block 5 | Crew Dragon Inflight Abort Test,StatusActive,"50.0 ",Success
                        58,Arianespace,"ELA-3, Guiana Space Centre, French Guiana, France","Thu Jan 16, 2020",Ariane 5 ECA | Eutelsat Konnect BB4A & GSAT-30,StatusActive,"200.0 ",Success
                        59,ExPace,"Site 95, Jiuquan Satellite Launch Center, China","Thu Jan 16, 2020",Kuaizhou 1A | Yinhe-1,StatusActive,,Success
                        60,CASC,"LC-9, Taiyuan Satellite Launch Center, China","Wed Jan 15, 2020",Long March 2D | Jilin-1 Wideband 01 & ??uSat-7/8,StatusActive,"29.75 ",Success
                        61,CASC,"LC-2, Xichang Satellite Launch Center, China","Tue Jan 07, 2020",Long March 3B/E | TJSW-5,StatusActive,"29.15 ",Success
                        62,SpaceX,"SLC-40, Cape Canaveral AFS, Florida, USA","Tue Jan 07, 2020",Falcon 9 Block 5 | Starlink V1 L2,StatusActive,"50.0 ",Success
                        63,CASC,"LC-101, Wenchang Satellite Launch Center, China","Fri Dec 27, 2019",Long March 5 | Shijian-20,StatusActive,,Success
                        64,VKS RF,"Site 133/3, Plesetsk Cosmodrome, Russia","Thu Dec 26, 2019","Rokot/Briz KM | Gonets-M ???24, 25, 26 [block-15] & Blits-M1",StatusRetired,"41.8 ",Success
                        65,Roscosmos,"Site 81/24, Baikonur Cosmodrome, Kazakhstan","Tue Dec 24, 2019",Proton-M/DM-3 | Elektro-L n†\u00AD3,StatusActive,"65.0 ",Success
                        66,ULA,"SLC-41, Cape Canaveral AFS, Florida, USA","Fri Dec 20, 2019",Atlas V N22 | Starliner OFT,StatusActive,,Success
                        67,CASC,"LC-9, Taiyuan Satellite Launch Center, China","Fri Dec 20, 2019","Long March 4B | CBERS-4A, ETRSS-1 & Others",StatusActive,"64.68 ",Success
                        68,Arianespace,"ELS, Guiana Space Centre, French Guiana, France","Wed Dec 18, 2019","Soyuz ST-A/Fregat-M | CSG-1, CHEOPS & Others",StatusActive,,Success
                        69,SpaceX,"SLC-40, Cape Canaveral AFS, Florida, USA","Tue Dec 17, 2019",Falcon 9 Block 5 | JCSAT-18 / Kacific-1,StatusActive,"50.0 ",Success
                        70,CASC,"LC-3, Xichang Satellite Launch Center, China","Mon Dec 16, 2019",Long March 3B/YZ-1 | BeiDou-3 M19 & M20,StatusActive,,Success
                        71,Blue Origin,"Blue Origin Launch Site, West Texas, Texas, USA","Wed Dec 11, 2019",New Shepard | NS-12,StatusActive,,Success
                        72,ISRO,"First Launch Pad, Satish Dhawan Space Centre, India","Wed Dec 11, 2019",PSLV-QL | RISAT 2BR1,StatusActive,"21.0 ",Success
                        73,VKS RF,"Site 43/3, Plesetsk Cosmodrome, Russia","Wed Dec 11, 2019",Soyuz 2.1b/Fregat | Cosmos 2544,StatusActive,"48.5 ",Success
                        74,ExPace,"Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China","Sat Dec 07, 2019",Kuaizhou 1A | HEAD-2A/B / SpaceTY 16/17 / Tianqi 4A/B,StatusActive,,Success
                        75,ExPace,"Taiyuan Satellite Launch Center, China","Sat Dec 07, 2019",Kuaizhou 1A | Jilin-1 Gaofen-02B,StatusActive,,Success
                        76,Roscosmos,"Site 31/6, Baikonur Cosmodrome, Kazakhstan","Fri Dec 06, 2019",Soyuz 2.1a | Progress MS-13 (74P),StatusActive,"48.5 ",Success
                        77,Rocket Lab,"Rocket Lab LC-1A, M?\u0081hia Peninsula, New Zealand","Fri Dec 06, 2019",Electron/Curie | Running Out Of Fingers,StatusActive,"7.5 ",Success
                        78,SpaceX,"SLC-40, Cape Canaveral AFS, Florida, USA","Thu Dec 05, 2019",Falcon 9 Block 5 | CRS-19,StatusActive,"50.0 ",Success
                        79,CASC,"LC-9, Taiyuan Satellite Launch Center, China","Wed Nov 27, 2019",Long March 4C | Gaofen-12,StatusActive,"64.68 ",Success
                        80,ISRO,"Second Launch Pad, Satish Dhawan Space Centre, India","Wed Nov 27, 2019",PSLV-XL | Cartosat-3 & Rideshares,StatusActive,"31.0 ",Success
                        81,Arianespace,"ELA-3, Guiana Space Centre, French Guiana, France","Tue Nov 26, 2019",Ariane 5 ECA | Inmarsat 5 F5 & TIBA-1,StatusActive,"200.0 ",Success
                        82,VKS RF,"Site 43/4, Plesetsk Cosmodrome, Russia","Mon Nov 25, 2019",Soyuz 2.1v/Volga | Cosmos 2542 & 2543,StatusActive,,Success
                        83,CASC,"LC-3, Xichang Satellite Launch Center, China","Sat Nov 23, 2019",Long March 3B/YZ-1 | BeiDou-3 M21 & M22,StatusActive,,Success
                        84,ExPace,"Site 95, Jiuquan Satellite Launch Center, China","Sun Nov 17, 2019","Kuaizhou 1A | KL-Alpha A, KL-Alpha B",StatusActive,,Success
                        85,CASC,"LC-16, Taiyuan Satellite Launch Center, China","Wed Nov 13, 2019",Long March 6 | Ningxia-1 (x5),StatusActive,,Success
                        86,ExPace,"Site 95, Jiuquan Satellite Launch Center, China","Wed Nov 13, 2019",Kuaizhou 1A | Jilin 1-02A,StatusActive,,Success
                        87,SpaceX,"SLC-40, Cape Canaveral AFS, Florida, USA","Mon Nov 11, 2019",Falcon 9 Block 5 | Starlink V1 L1,StatusActive,"50.0 ",Success
                        88,CASC,"LC-2, Xichang Satellite Launch Center, China","Mon Nov 04, 2019",Long March 3B/E | Beidou-3 IGSO-3,StatusActive,"29.15 ",Success
                        89,CASC,"LC-9, Taiyuan Satellite Launch Center, China","Sun Nov 03, 2019",Long March 4B | Gaofen-7,StatusActive,"64.68 ",Success
                        90,Northrop,"LP-0A, Wallops Flight Facility, Virginia, USA","Sat Nov 02, 2019",Antares 230+ | CRS NG-12,StatusActive,"85.0 ",Success
                        91,Exos,"Vertical Launch Area, Spaceport America, New Mexico","Sat Oct 26, 2019",SARGE | Launch 4,StatusActive,,Failure
                        92,CASC,"LC-3, Xichang Satellite Launch Center, China","Thu Oct 17, 2019",Long March 3B/E | TJSW-4,StatusActive,"29.15 ",Success
                        93,Rocket Lab,"Rocket Lab LC-1A, M?\u0081hia Peninsula, New Zealand","Thu Oct 17, 2019",Electron/Curie | As The Crow Flies,StatusActive,"7.5 ",Success
                        94,Northrop,"Stargazer, Cape Canaveral AFS, Florida, USA","Fri Oct 11, 2019",Pegasus XL | ICON,StatusActive,"40.0 ",Success
                        95,ILS,"Site 200/39, Baikonur Cosmodrome, Kazakhstan","Wed Oct 09, 2019",Proton-M/Briz-M | Eutelsat 5 West B & MEV-1,StatusActive,"65.0 ",Success
                        96,CASC,"LC-9, Taiyuan Satellite Launch Center, China","Fri Oct 04, 2019",Long March 4C | Gaofen 10 (Replacement),StatusActive,"64.68 ",Success
                        97,VKS RF,"Site 43/4, Plesetsk Cosmodrome, Russia","Thu Sep 26, 2019",Soyuz 2.1b/Fregat | Cosmos 2541,StatusActive,"48.5 ",Success
                        98,Roscosmos,"Site 1/5, Baikonur Cosmodrome, Kazakhstan","Wed Sep 25, 2019",Soyuz FG | Soyuz MS-15 (61S),StatusRetired,,Success
                        99,CASC,"Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China","Wed Sep 25, 2019",Long March 2D | Yunhai-1-02,StatusActive,"29.75 ",Success
                        100,MHI,"LA-Y2, Tanegashima Space Center, Japan","Tue Sep 24, 2019",H-IIB | HTV-8,StatusRetired,"112.5 ",Success
                        101,CASC,"LC-2, Xichang Satellite Launch Center, China","Sun Sep 22, 2019",Long March 3B/YZ-1 | BeiDou-3 M23 & M24,StatusActive,,Success
                        102,CASC,"Site 95, Jiuquan Satellite Launch Center, China","Thu Sep 19, 2019",Long March 11 | Zhuhai-1 Group 03,StatusActive,"5.3 ",Success
                        103,CASC,"LC-9, Taiyuan Satellite Launch Center, China","Thu Sep 12, 2019","Long March 4B | Ziyuan-2D, BNU-1 & Taurus-1",StatusActive,"64.68 ",Success
                        104,ExPace,"Site 95, Jiuquan Satellite Launch Center, China","Fri Aug 30, 2019",Kuaizhou 1A | KX-09 & Others,StatusActive,,Success
                        105,VKS RF,"Site 133/3, Plesetsk Cosmodrome, Russia","Fri Aug 30, 2019",Rokot/Briz KM | Cosmos 2540,StatusRetired,"41.8 ",Success
                        106,ISA,"Imam Khomeini Spaceport, Semnan Space Center, Iran","Thu Aug 29, 2019",Safir-1B+ | Nahid-1,StatusActive,,Prelaunch Failure
                        107,ULA,"SLC-37B, Cape Canaveral AFS, Florida, USA","Thu Aug 22, 2019","Delta IV Medium+ (4,2) | GPS III SV02",StatusRetired,"164.0 ",Success
                        108,Roscosmos,"Site 31/6, Baikonur Cosmodrome, Kazakhstan","Thu Aug 22, 2019",Soyuz 2.1a | Soyuz MS-14 (60S),StatusActive,"48.5 ",Success
                        109,Rocket Lab,"Rocket Lab LC-1A, M?\u0081hia Peninsula, New Zealand","Mon Aug 19, 2019","Electron/Curie | Look Ma, No Hands!",StatusActive,"7.5 ",Success
                        110,CASC,"LC-2, Xichang Satellite Launch Center, China","Mon Aug 19, 2019",Long March 3B/E | ChinaSat 18,StatusActive,"29.15 ",Success
                        111,CASC,"Site 95, Jiuquan Satellite Launch Center, China","Sat Aug 17, 2019",Jielong-1 | Tianqi-4 & Others,StatusActive,"7.5 ",Success
                        112,ULA,"SLC-41, Cape Canaveral AFS, Florida, USA","Thu Aug 08, 2019",Atlas V 551 | AEHF 5,StatusActive,"153.0 ",Success
                        113,SpaceX,"SLC-40, Cape Canaveral AFS, Florida, USA","Tue Aug 06, 2019",Falcon 9 Block 5 | AMOS-17,StatusActive,"50.0 ",Success
                        114,Arianespace,"ELA-3, Guiana Space Centre, French Guiana, France","Tue Aug 06, 2019",Ariane 5 ECA | EDRS-C/HYLAS 3 & Intelsat 39,StatusActive,"200.0 ",Success
                        115,Roscosmos,"Site 81/24, Baikonur Cosmodrome, Kazakhstan","Mon Aug 05, 2019",Proton-M/Briz-M | Cosmos 2539,StatusActive,"65.0 ",Success
                        116,Roscosmos,"Site 31/6, Baikonur Cosmodrome, Kazakhstan","Wed Jul 31, 2019",Soyuz 2.1a | Progress MS-12,StatusActive,"48.5 ",Success
                        117,VKS RF,"Site 43/4, Plesetsk Cosmodrome, Russia","Tue Jul 30, 2019",Soyuz 2.1a/Fregat-M | Meridian-M n†\u00AD18L,StatusActive,"48.5 ",Success
                        118,CASC,"LC-3, Xichang Satellite Launch Center, China","Fri Jul 26, 2019",Long March 2C | Yaogan-30-05,StatusActive,"30.8 ",Success
                        119,SpaceX,"SLC-40, Cape Canaveral AFS, Florida, USA","Thu Jul 25, 2019",Falcon 9 Block 5 | CRS-18,StatusActive,"50.0 ",Success
                        120,i-Space,"Site 95, Jiuquan Satellite Launch Center, China","Thu Jul 25, 2019",Hyperbola-1 | CAS-7B & Others,StatusActive,,Success
                        121,ISRO,"Second Launch Pad, Satish Dhawan Space Centre, India","Mon Jul 22, 2019",GSLV Mk III | Chandrayaan-2 lunar mission,StatusActive,"62.0 ",Success
                        122,Roscosmos,"Site 1/5, Baikonur Cosmodrome, Kazakhstan","Sat Jul 20, 2019",Soyuz FG | Soyuz MS-13 (59S),StatusRetired,,Success
                        123,Roscosmos,"Site 81/24, Baikonur Cosmodrome, Kazakhstan","Sat Jul 13, 2019",Proton-M/DM-3 | Spektr-RG,StatusActive,"65.0 ",Success
                        124,Arianespace,"ELV-1 (SLV), Guiana Space Centre, French Guiana, France","Thu Jul 11, 2019",Vega | Falcon Eye 1,StatusActive,"37.0 ",Failure
                        125,VKS RF,"Site 43/4, Plesetsk Cosmodrome, Russia","Wed Jul 10, 2019",Soyuz 2.1v/Volga | Cosmos 2535 to 2538,StatusActive,,Success
                        126,Roscosmos,"Site 1S, Vostochny Cosmodrome, Russia","Fri Jul 05, 2019","Soyuz 2.1b/Fregat-M | Meteor-M No.2-2, Landmapper-BC 5 & 6, and Others",StatusActive,,Success
                        127,Exos,"Vertical Launch Area, Spaceport America, New Mexico","Sat Jun 29, 2019",SARGE | Launch 3,StatusActive,,Partial Failure
                        128,Rocket Lab,"Rocket Lab LC-1A, M?\u0081hia Peninsula, New Zealand","Sat Jun 29, 2019",Electron/Curie | Make it Rain,StatusActive,"7.5 ",Success
                        129,SpaceX,"LC-39A, Kennedy Space Center, Florida, USA","Tue Jun 25, 2019",Falcon Heavy | STP-2,StatusActive,"90.0 ",Success
                        130,CASC,"LC-3, Xichang Satellite Launch Center, China","Mon Jun 24, 2019",Long March 3B/E | Beidou-3 IGSO-2,StatusActive,"29.15 ",Success
                        131,Arianespace,"ELA-3, Guiana Space Centre, French Guiana, France","Thu Jun 20, 2019",Ariane 5 ECA | Eutelsat 7C &  AT&T T-16,StatusActive,"200.0 ",Success
                        132,SpaceX,"SLC-4E, Vandenberg AFB, California, USA","Wed Jun 12, 2019",Falcon 9 Block 5 | RADARSAT Constellation,StatusActive,"50.0 ",Success
                        133,CASC,"Tai Rui Barge, Yellow Sea","Wed Jun 05, 2019",Long March 11H | Jilin-1 & Others,StatusActive,"5.3 ",Success
                        134,Roscosmos,"Site 200/39, Baikonur Cosmodrome, Kazakhstan","Thu May 30, 2019",Proton-M/Briz-M | Yamal-601,StatusActive,"65.0 ",Success
                        135,Roscosmos,"Site 43/4, Plesetsk Cosmodrome, Russia","Mon May 27, 2019",Soyuz 2.1b/Fregat-M | Cosmos 2534,StatusActive,,Success
                        136,SpaceX,"SLC-40, Cape Canaveral AFS, Florida, USA","Fri May 24, 2019",Falcon 9 Block 5 | Starlink V0.9,StatusActive,"50.0 ",Success
                        137,CASC,"LC-9, Taiyuan Satellite Launch Center, China","Wed May 22, 2019",Long March 4C | Yaogan Weixing-33,StatusActive,"64.68 ",Failure
                        138,ISRO,"First Launch Pad, Satish Dhawan Space Centre, India","Wed May 22, 2019",PSLV-CA | RISAT-2B,StatusActive,"21.0 ",Success
                        139,CASC,"LC-2, Xichang Satellite Launch Center, China","Fri May 17, 2019",Long March 3C/E | Beidou-2 G8,StatusActive,,Success
                        140,Rocket Lab,"Rocket Lab LC-1A, M?\u0081hia Peninsula, New Zealand","Sun May 05, 2019",Electron/Curie | That's a Funny Looking Cactus,StatusActive,"7.5 ",Success
                        141,SpaceX,"SLC-40, Cape Canaveral AFS, Florida, USA","Sat May 04, 2019",Falcon 9 Block 5 | CRS-17,StatusActive,"50.0 ",Success
                        142,Blue Origin,"Blue Origin Launch Site, West Texas, Texas, USA","Thu May 02, 2019",New Shepard | NS-11,StatusActive,,Success
                        143,CASC,"Taiyuan Satellite Launch Center, China","Mon Apr 29, 2019",Long March 4B | Tianhui-2 Group 01,StatusActive,"64.68 ",Success
                        144,CASC,"LC-3, Xichang Satellite Launch Center, China","Sat Apr 20, 2019",Long March 3B/E | Beidou-3 IGSO-1,StatusActive,"29.15 ",Success
                        145,Northrop,"LP-0A, Wallops Flight Facility, Virginia, USA","Wed Apr 17, 2019",Antares 230 | CRS NG-11,StatusRetired,"85.0 ",Success
                        146,SpaceX,"LC-39A, Kennedy Space Center, Florida, USA","Thu Apr 11, 2019",Falcon Heavy | ArabSat 6A,StatusActive,"90.0 ",Success
                        147,Arianespace,"ELS, Guiana Space Centre, French Guiana, France","Thu Apr 04, 2019",Soyuz ST-B/Fregat-MT | O3b FM17-FM20,StatusActive,,Success
                        148,Roscosmos,"Site 31/6, Baikonur Cosmodrome, Kazakhstan","Thu Apr 04, 2019",Soyuz 2.1a | Progress MS-11 (72P),StatusActive,"48.5 ",Success""";

        // rockets will be created based on this data set
        rocketDataSet =
                """
                        0,Tsyklon-3,https://en.wikipedia.org/wiki/Tsyklon-3,39.0 m
                        1,Tsyklon-4M,https://en.wikipedia.org/wiki/Cyclone-4M,38.7 m
                        2,Unha-2,https://en.wikipedia.org/wiki/Unha,28.0 m
                        3,Unha-3,https://en.wikipedia.org/wiki/Unha,32.0 m
                        4,Vanguard,https://en.wikipedia.org/wiki/Vanguard_(rocket),23.0 m
                        5,Vector-H,https://en.wikipedia.org/wiki/Vector-H,18.3 m
                        6,Vector-R,https://en.wikipedia.org/wiki/Vector-R,13.0 m
                        7,Vega,https://en.wikipedia.org/wiki/Vega_(rocket),29.9 m
                        8,Vega C,https://en.wikipedia.org/wiki/Vega_(rocket),35.0 m
                        9,Vega E,https://en.wikipedia.org/wiki/Vega_(rocket),35.0 m
                        10,VLS-1,https://en.wikipedia.org/wiki/VLS-1,19.0 m
                        11,Volna,https://en.wikipedia.org/wiki/Volna,15.0 m
                        12,Voskhod,https://en.wikipedia.org/wiki/Voskhod_(rocket),31.0 m
                        13,Vostok,https://en.wikipedia.org/wiki/Vostok-K,31.0 m
                        14,Vostok-2,https://en.wikipedia.org/wiki/Vostok-2_(rocket),
                        15,Vostok-2A,https://en.wikipedia.org/wiki/Vostok_(rocket_family),
                        16,Vostok-2M,https://en.wikipedia.org/wiki/Vostok-2M,
                        17,Vulcan Centaur,https://en.wikipedia.org/wiki/Vulcan_%28rocket%29,58.3 m
                        18,Zenit-2,https://en.wikipedia.org/wiki/Zenit-2,57.0 m
                        19,Zenit-2 FG,https://en.wikipedia.org/wiki/Zenit_%28rocket_family%29,57.0 m
                        20,Zenit-3 SL,https://en.wikipedia.org/wiki/Zenit_%28rocket_family%29,59.6 m
                        21,Zenit-3 SLB,https://en.wikipedia.org/wiki/Zenit_%28rocket_family%29,57.0 m
                        22,Zenit-3 SLBF,https://en.wikipedia.org/wiki/Zenit-3F,57.0 m
                        23,Zéphyr,https://fr.wikipedia.org/wiki/Z%C3%A9phyr_(fus%C3%A9e),12.3 m
                        24,ZhuQue-1,https://en.wikipedia.org/wiki/LandSpace,19.0 m
                        25,ZhuQue-2,https://en.wikipedia.org/wiki/LandSpace#Zhuque-2,
                        26,Angara 1.1,https://en.wikipedia.org/wiki/Angara_(rocket_family),35.0 m
                        27,Angara 1.2,https://en.wikipedia.org/wiki/Angara_(rocket_family),41.5 m
                        28,Angara A5/Briz-M,https://en.wikipedia.org/wiki/Angara_(rocket_family)#Angara_A5,
                        29,Angara A5/DM-03,https://en.wikipedia.org/wiki/Angara_(rocket_family)#Angara_A5,
                        30,Angara A5M,https://en.wikipedia.org/wiki/Angara_(rocket_family)#Angara_A5,
                        31,Antares 110,https://en.wikipedia.org/wiki/Antares_(rocket),40.5 m
                        32,Antares 120,https://en.wikipedia.org/wiki/Antares_(rocket),40.5 m
                        33,Antares 130,https://en.wikipedia.org/wiki/Antares_(rocket),40.5 m
                        34,Antares 230,https://en.wikipedia.org/wiki/Antares_(rocket),41.9 m
                        35,Antares 230+,https://en.wikipedia.org/wiki/Antares_%28rocket%29#Antares_230+,42.5 m
                        36,Ares 1-X,https://en.wikipedia.org/wiki/Ares_I,94.0 m
                        37,Ariane 1,https://en.wikipedia.org/wiki/Ariane_1,50.0 m
                        38,Ariane 2,https://en.wikipedia.org/wiki/Ariane_2,49.13 m
                        39,Ariane 3,https://en.wikipedia.org/wiki/Ariane_3,49.13 m
                        40,Ariane 40,https://en.wikipedia.org/wiki/Ariane_4,58.72 m
                        41,Ariane 42L,https://en.wikipedia.org/wiki/Ariane_4,58.72 m
                        42,Ariane 42P,https://en.wikipedia.org/wiki/Ariane_4,58.72 m
                        43,Ariane 44L,https://en.wikipedia.org/wiki/Ariane_4,58.72 m
                        44,Ariane 44LP,https://en.wikipedia.org/wiki/Ariane_4,58.72 m
                        45,Ariane 44P,https://en.wikipedia.org/wiki/Ariane_4,58.72 m
                        46,Ariane 5 ECA,https://en.wikipedia.org/wiki/Ariane_5,53.0 m
                        47,Ariane 5 ES,https://en.wikipedia.org/wiki/Ariane_5,50.5 m
                        48,Ariane 5 G,https://en.wikipedia.org/wiki/Ariane_5,52.0 m
                        49,Ariane 5 G+,https://en.wikipedia.org/wiki/Ariane_5,
                        50,Ariane 5 GS,https://en.wikipedia.org/wiki/Ariane_5,
                        51,Ariane 62,https://en.wikipedia.org/wiki/Ariane_6,63.0 m
                        52,Ariane 64,https://en.wikipedia.org/wiki/Ariane_6,63.0 m
                        53,Ariane 64 / Icarus,https://en.wikipedia.org/wiki/Ariane_6,63.0 m
                        54,ASLV,https://en.wikipedia.org/wiki/Augmented_Satellite_Launch_Vehicle,24.0 m
                        55,Athena I,https://en.wikipedia.org/wiki/Athena_I,18.9 m
                        56,Athena II,https://en.wikipedia.org/wiki/Athena_II,28.2 m
                        57,Atlas-D Able,https://en.wikipedia.org/wiki/Atlas-Able,35.0 m
                        58,Atlas-D Mercury,https://en.wikipedia.org/wiki/Atlas_LV-3B,28.7 m
                        59,Atlas-D OV1,https://en.wikipedia.org/wiki/SM-65D_Atlas,
                        60,Atlas-E/F Agena D,https://en.wikipedia.org/wiki/Atlas-Agena,
                        61,Atlas-E/F Altair,https://en.wikipedia.org/wiki/Atlas_E/F,
                        62,Atlas-E/F Burner,,
                        63,Atlas-E/F MSD,https://en.wikipedia.org/wiki/Atlas_E/F#Atlas_E/F-MSD,
                        64,Atlas-E/F OIS,https://en.wikipedia.org/wiki/Atlas_E/F#Atlas_E/F-OIS,
                        65,Atlas-E/F OV1,https://en.wikipedia.org/wiki/Atlas_E/F#Atlas_E/F-OV1,
                        66,Atlas-E/F PTS,https://en.wikipedia.org/wiki/Atlas_E/F#Atlas_E/F-PTS,
                        67,Atlas-E/F SGS-1,https://en.wikipedia.org/wiki/Atlas_E/F#Atlas_E/F-SGS,
                        68,Atlas-E/F SGS-2,https://en.wikipedia.org/wiki/Atlas_E/F#Atlas_E/F-SGS,
                        69,Atlas-E/F Star-17A,https://en.wikipedia.org/wiki/Atlas_E/F#Atlas_E/F-Star,
                        70,Atlas-E/F Star-37S-ISS,https://en.wikipedia.org/wiki/Atlas_E/F#Atlas_E/F-Star,
                        71,Atlas-G Centaur-D1AR,https://en.wikipedia.org/wiki/Atlas_G,
                        72,Atlas-H MSD,https://en.wikipedia.org/wiki/Atlas_H,
                        73,Atlas I,https://en.wikipedia.org/wiki/Atlas_I,43.9 m
                        74,Atlas II,https://en.wikipedia.org/wiki/List_of_Atlas_launches_(1990–1999),47.5 m
                        75,Atlas IIA,https://en.wikipedia.org/wiki/List_of_Atlas_launches_(1990–1999),47.5 m
                        76,Atlas IIAS,https://en.wikipedia.org/wiki/List_of_Atlas_launches_(1990–1999),47.5 m
                        77,Atlas IIIA,https://en.wikipedia.org/wiki/Atlas_III,52.8 m
                        78,Atlas IIIB,https://en.wikipedia.org/wiki/Atlas_III,52.8 m
                        79,Atlas-LV3 Agena-A,https://en.wikipedia.org/wiki/Atlas-Agena,
                        80,Atlas-LV3 Agena-B,https://en.wikipedia.org/wiki/Atlas-Agena,
                        81,Atlas-LV3 Agena-D,https://en.wikipedia.org/wiki/Atlas-Agena,
                        82,Atlas-LV3C Centaur-A,https://en.wikipedia.org/wiki/Atlas-Centaur,
                        83,Atlas-LV3C Centaur-B,https://en.wikipedia.org/wiki/Atlas-Centaur,
                        84,Atlas-LV3C Centaur-C,https://en.wikipedia.org/wiki/Atlas-Centaur,
                        85,Atlas-LV3C Centaur-D,https://en.wikipedia.org/wiki/Atlas-Centaur,
                        86,Atlas SLV-3,https://en.wikipedia.org/wiki/Atlas_SLV-3,
                        87,Atlas-SLV3A Agena-D,https://en.wikipedia.org/wiki/Atlas-Agena,
                        88,Atlas-SLV3 Agena-B,https://en.wikipedia.org/wiki/Atlas-Agena,
                        89,Atlas-SLV3 Agena-D,https://en.wikipedia.org/wiki/Atlas-Agena,
                        90,Atlas-SLV3B Agena-D,https://en.wikipedia.org/wiki/Atlas-Agena,
                        91,Atlas-SLV3 Burner-2,https://en.wikipedia.org/wiki/Atlas_SLV-3,
                        92,Atlas-SLV3C Centaur-D,https://en.wikipedia.org/wiki/Atlas-Centaur,
                        93,Atlas-SLV3D Centaur-D1A,https://en.wikipedia.org/wiki/Atlas-Centaur,
                        94,Atlas-SLV3D Centaur-D1AR,https://en.wikipedia.org/wiki/Atlas-Centaur,
                        95,Atlas V 401,https://en.wikipedia.org/wiki/Atlas_V,58.3 m
                        96,Atlas V 411,https://en.wikipedia.org/wiki/Atlas_V,58.3 m
                        97,Atlas V 421,https://en.wikipedia.org/wiki/Atlas_V,58.3 m
                        98,Atlas V 431,https://en.wikipedia.org/wiki/Atlas_V,59.1 m
                        99,Atlas V 501,https://en.wikipedia.org/wiki/Atlas_V,62.2 m
                        100,Atlas V 511,https://en.wikipedia.org/wiki/Atlas_V,62.2 m
                        101,Atlas V 521,https://en.wikipedia.org/wiki/Atlas_V,62.2 m
                        102,Atlas V 531,https://en.wikipedia.org/wiki/Atlas_V,62.2 m
                        103,Atlas V 541,https://en.wikipedia.org/wiki/Atlas_V,62.2 m
                        104,Atlas V 551,https://en.wikipedia.org/wiki/Atlas_V,62.2 m
                        105,Atlas V 552,https://en.wikipedia.org/wiki/Atlas_V,62.2 m
                        106,Atlas V N22,https://en.wikipedia.org/wiki/Atlas_V,
                        107,Black Arrow,https://en.wikipedia.org/wiki/Black_Arrow,13.0 m
                        108,Blue Scout II,https://en.wikipedia.org/wiki/RM-90_Blue_Scout_II,24.0 m
                        109,Ceres-1,,19.0 m
                        110,Commercial Titan III,https://en.wikipedia.org/wiki/Commercial_Titan_III,47.0 m
                        111,Conestoga-1620,https://en.wikipedia.org/wiki/Conestoga_(rocket),
                        112,Cosmos-1 (65S3),https://en.wikipedia.org/wiki/Kosmos_(rocket_family),31.0 m
                        113,Cosmos-2I (63S1),https://en.wikipedia.org/wiki/Kosmos_(rocket_family),31.0 m
                        114,Cosmos-2I (63SM),https://en.wikipedia.org/wiki/Kosmos-2I,31.0 m
                        115,Cosmos-3 (11K65),https://en.wikipedia.org/wiki/Kosmos-3,26.0 m
                        116,Cosmos-3M (11K65M),https://en.wikipedia.org/wiki/Kosmos-3M,32.0 m
                        117,Cosmos-3MRB (65MRB),https://en.wikipedia.org/wiki/Kosmos-3,26.0 m
                        118,Delta 3920-8,https://en.wikipedia.org/wiki/Delta_3000,
                        119,Delta 4925-8,https://en.wikipedia.org/wiki/Delta_(rocket_family)#Delta_4000-Series,34.0 m
                        120,Delta A,https://en.wikipedia.org/wiki/Delta_A,
                        121,Delta B,https://en.wikipedia.org/wiki/Delta_B,
                        122,Delta C,https://en.wikipedia.org/wiki/Delta_C,31.0 m
                        123,Delta II 6920-10,https://en.wikipedia.org/wiki/Delta_II,38.9 m
                        124,Delta II 6920-8,https://en.wikipedia.org/wiki/Delta_II,38.1 m
                        125,Delta II 6925,https://en.wikipedia.org/wiki/Delta_II,38.1 m
                        126,Delta II 6925-8,https://en.wikipedia.org/wiki/Delta_II,38.1 m
                        127,Delta II 7320-10C,https://en.wikipedia.org/wiki/Delta_II,38.9 m
                        128,Delta II 7326,https://en.wikipedia.org/wiki/Delta_II,38.1 m
                        129,Delta II 7420-10C,https://en.wikipedia.org/wiki/Delta_II,38.9 m
                        130,Delta II 7425,https://en.wikipedia.org/wiki/Delta_II,38.1 m
                        131,Delta II 7425-10C,https://en.wikipedia.org/wiki/Delta_II,38.9 m
                        132,Delta II 7426,https://en.wikipedia.org/wiki/Delta_II,38.1 m
                        133,Delta II 7920-10,https://en.wikipedia.org/wiki/Delta_II,38.9 m
                        134,Delta II 7920-10C,https://en.wikipedia.org/wiki/Delta_II,38.9 m
                        135,Delta II 7920-10L,https://en.wikipedia.org/wiki/Delta_II,39.3 m
                        136,Delta II 7920-8,https://en.wikipedia.org/wiki/Delta_II,38.1 m
                        137,Delta II 7920H,https://en.wikipedia.org/wiki/Delta_II,38.1 m
                        138,Delta II 7920H-10C,https://en.wikipedia.org/wiki/Delta_II,38.9 m
                        139,Delta II 7925,https://en.wikipedia.org/wiki/Delta_II,38.1 m
                        140,Delta II 7925-10,https://en.wikipedia.org/wiki/Delta_II,38.9 m
                        141,Delta II 7925-10C,https://en.wikipedia.org/wiki/Delta_II,38.9 m
                        142,Delta II 7925-10L,https://en.wikipedia.org/wiki/Delta_II,39.3 m
                        143,Delta II 7925-8,https://en.wikipedia.org/wiki/Delta_II,38.1 m
                        144,Delta II 7925H,https://en.wikipedia.org/wiki/Delta_II,38.1 m
                        145,Delta III 8930,https://en.wikipedia.org/wiki/Delta_III,35.0 m
                        146,Delta IV Heavy,https://en.wikipedia.org/wiki/Delta_IV_Heavy,72.0 m
                        147,Delta IV Medium,https://en.wikipedia.org/wiki/Delta_(rocket_family)#Delta_M,62.5 m
                        148,"Delta IV Medium+ (4,2)",https://en.wikipedia.org/wiki/Delta_IV,62.5 m
                        149,"Delta IV Medium+ (5,2)",https://en.wikipedia.org/wiki/Delta_IV,66.4 m
                        150,"Delta IV Medium+ (5,4)",https://en.wikipedia.org/wiki/Delta_IV,66.4 m
                        151,Diamant A,https://en.wikipedia.org/wiki/Diamant,16.95 m
                        152,Diamant B,https://en.wikipedia.org/wiki/Diamant,24.2 m
                        153,Diamant BP4,https://en.wikipedia.org/wiki/Diamant,21.64 m
                        154,Dnepr,https://en.wikipedia.org/wiki/Dnepr_(rocket),34.3 m
                        155,Electron,https://en.wikipedia.org/wiki/Electron_(rocket),17.0 m
                        156,Electron/Curie,https://en.wikipedia.org/wiki/Electron_(rocket),17.0 m
                        157,Electron/Photon,https://en.wikipedia.org/wiki/Electron_(rocket),17.0 m
                        158,Energiya/Buran,https://en.wikipedia.org/wiki/Energia#Development,59.0 m
                        159,Energiya/Polyus,https://en.wikipedia.org/wiki/Energia#Development,59.0 m
                        160,Epsilon,https://en.wikipedia.org/wiki/Epsilon_(rocket),26.0 m
                        161,Epsilon Demo,https://en.wikipedia.org/wiki/Epsilon_(rocket),24.0 m
                        162,Epsilon PBS,https://en.wikipedia.org/wiki/Epsilon_(rocket),26.0 m
                        163,Epsilon S,https://en.wikipedia.org/wiki/Epsilon_(rocket),27.0 m
                        164,Europa 1,https://en.wikipedia.org/wiki/Europa_(rocket),31.68 m
                        165,Europa 2,https://en.wikipedia.org/wiki/Europa_(rocket),31.68 m
                        166,Falcon 1,https://en.wikipedia.org/wiki/Falcon_1,22.25 m
                        167,Falcon 9 Block 3,https://en.wikipedia.org/wiki/Falcon_9,70.0 m
                        168,Falcon 9 Block 4,https://en.wikipedia.org/wiki/Falcon_9,70.0 m
                        169,Falcon 9 Block 5,https://en.wikipedia.org/wiki/Falcon_9,70.0 m
                        170,Falcon 9 v1.0,https://en.wikipedia.org/wiki/Falcon_9_v1.0,54.9 m
                        171,Falcon 9 v1.1,https://en.wikipedia.org/wiki/Falcon_9_v1.1,68.4 m
                        172,Falcon Heavy,https://en.wikipedia.org/wiki/Falcon_Heavy,70.0 m
                        173,Feng Bao 1,https://en.wikipedia.org/wiki/Feng_Bao_1,33.0 m
                        174,Firefly Alpha,https://en.wikipedia.org/wiki/Firefly_Alpha,29.0 m
                        175,Firefly Beta,https://en.wikipedia.org/wiki/Firefly_Beta,31.0 m
                        176,GSLV Mk I,https://en.wikipedia.org/wiki/Geosynchronous_Satellite_Launch_Vehicle,49.13 m
                        177,GSLV Mk II,https://en.wikipedia.org/wiki/Geosynchronous_Satellite_Launch_Vehicle,51.7 m
                        178,GSLV Mk III,https://en.wikipedia.org/wiki/Geosynchronous_Satellite_Launch_Vehicle_Mark_III,43.4 m
                        179,H-I (9 SO),https://en.wikipedia.org/wiki/H-I,42.0 m
                        180,H-II,https://en.wikipedia.org/wiki/H-II,49.0 m
                        181,H-II (2 SSB),https://en.wikipedia.org/wiki/H-II,49.0 m
                        182,H-IIA 202,https://en.wikipedia.org/wiki/H-IIA,53.0 m
                        183,H-IIA 2022,https://en.wikipedia.org/wiki/H-IIA,53.0 m
                        184,H-IIA 2024,https://en.wikipedia.org/wiki/H-IIA,53.0 m
                        185,H-IIA 204,https://en.wikipedia.org/wiki/H-IIA,
                        186,H-IIB,https://en.wikipedia.org/wiki/H-IIB,56.6 m
                        187,H-III 22,https://en.wikipedia.org/wiki/H3_(rocket),63.0 m
                        188,H-III 24,https://en.wikipedia.org/wiki/H3_(rocket),50.0 m
                        189,H-III 30,https://en.wikipedia.org/wiki/H3_(rocket),63.0 m
                        190,H-IIS,https://en.wikipedia.org/wiki/H-II,49.0 m
                        191,H-I UM-129A (6SO),https://en.wikipedia.org/wiki/H-I,42.0 m
                        192,H-I UM-129A (9SO),https://en.wikipedia.org/wiki/H-I,42.0 m
                        193,Hyperbola-1,https://en.wikipedia.org/wiki/I-Space_(Chinese_company)#Hyperbola-1,21.0 m
                        194,Jielong-1,,
                        195,Juno I,https://en.wikipedia.org/wiki/Juno_I,21.2 m
                        196,Juno II,https://en.wikipedia.org/wiki/Juno_II,24.0 m
                        197,Kaituozhe 1,https://en.wikipedia.org/wiki/Kaituozhe_(rocket_family),
                        198,Kaituozhe 2,https://en.wikipedia.org/wiki/Kaituozhe_(rocket_family)#KT-2,
                        199,Kuaizhou 1,https://en.wikipedia.org/wiki/Kuaizhou,
                        200,Kuaizhou 11,https://en.wikipedia.org/wiki/Kuaizhou,25.0 m
                        201,Kuaizhou 1A,https://en.wikipedia.org/wiki/Kuaizhou,19.4 m
                        202,Lambda-IV S,https://en.wikipedia.org/wiki/Lambda_(rocket_family),16.5 m
                        203,LauncherOne,https://en.wikipedia.org/wiki/LauncherOne,16.0 m
                        204,Long March 1,https://en.wikipedia.org/wiki/Long_March_1,30.45 m
                        205,Long March 11,https://en.wikipedia.org/wiki/Long_March_11,20.8 m
                        206,Long March 11A,https://en.wikipedia.org/wiki/Long_March_11,
                        207,Long March 11H,https://en.wikipedia.org/wiki/Long_March_11,20.8 m
                        208,Long March 2,https://en.wikipedia.org/wiki/Long_March_2A,32.0 m
                        209,Long March 2C,https://en.wikipedia.org/wiki/Long_March_2C,42.0 m
                        210,Long March 2C/E,https://en.wikipedia.org/wiki/Long_March_2C,42.0 m
                        211,Long March 2C/SMA,https://en.wikipedia.org/wiki/Long_March_2C,
                        212,Long March 2C/YZ-1S,https://en.wikipedia.org/wiki/Long_March_2C,42.0 m
                        213,Long March 2D,https://en.wikipedia.org/wiki/Long_March_2D,41.06 m
                        214,Long March 2D/YZ-3,https://en.wikipedia.org/wiki/Long_March_2D,
                        215,Long March 2E,https://en.wikipedia.org/wiki/Long_March_2E,49.7 m
                        216,Long March 2F,https://en.wikipedia.org/wiki/Long_March_2F,62.0 m
                        217,Long March 2F/G,https://en.wikipedia.org/wiki/Long_March_2F,62.0 m
                        218,Long March 2F/T,https://en.wikipedia.org/wiki/Long_March_2F,58.0 m
                        219,Long March 3,https://en.wikipedia.org/wiki/Long_March_3,43.25 m
                        220,Long March 3A,https://en.wikipedia.org/wiki/Long_March_3A,52.52 m
                        221,Long March 3B,https://en.wikipedia.org/wiki/Long_March_3B,54.8 m
                        222,Long March 3B/E,https://en.wikipedia.org/wiki/Long_March_3B,56.3 m
                        223,Long March 3B/YZ-1,https://en.wikipedia.org/wiki/Long_March_3B,
                        224,Long March 3C,https://en.wikipedia.org/wiki/Long_March_3C,55.64 m
                        225,Long March 3C/E,https://en.wikipedia.org/wiki/Long_March_3C,54.8 m
                        226,Long March 3C/YZ-1,https://en.wikipedia.org/wiki/Long_March_3C,
                        227,Long March 4A,https://en.wikipedia.org/wiki/Long_March_4A,41.9 m
                        228,Long March 4B,https://en.wikipedia.org/wiki/Long_March_4B,44.1 m
                        229,Long March 4C,https://en.wikipedia.org/wiki/Long_March_4C,45.8 m
                        230,Long March 5,https://en.wikipedia.org/wiki/Long_March_5,57.0 m
                        231,Long March 5B,https://en.wikipedia.org/wiki/Long_March_5,53.66 m
                        232,Long March 5/YZ-2,https://en.wikipedia.org/wiki/Long_March_5,57.0 m
                        233,Long March 6,https://en.wikipedia.org/wiki/Long_March_6,29.24 m
                        234,Long March 6A,https://en.wikipedia.org/wiki/Long_March_6,29.24 m
                        235,Long March 7,https://en.wikipedia.org/wiki/Long_March_7,53.1 m
                        236,Long March 7A,https://en.wikipedia.org/wiki/Long_March_7,
                        237,Long March 7/YZ-1A,https://en.wikipedia.org/wiki/Long_March_7,53.1 m
                        238,Long March 8,https://en.wikipedia.org/wiki/Long_March_(rocket_family)#Long_March_8,
                        239,Long March 9,https://en.wikipedia.org/wiki/Long_March_(rocket_family)#Variants,110.0 m
                        240,Mercury-Redstone,https://en.wikipedia.org/wiki/Mercury-Redstone_Launch_Vehicle,25.4 m
                        241,Minotaur C (Taurus),https://en.wikipedia.org/wiki/Minotaur-C,27.9 m
                        242,Minotaur I,https://en.wikipedia.org/wiki/Minotaur_I,19.21 m
                        243,Minotaur IV,https://en.wikipedia.org/wiki/Minotaur_IV,23.88 m
                        244,Minotaur V,https://en.wikipedia.org/wiki/Minotaur_V,24.56 m
                        245,Miura 5,https://en.wikipedia.org/wiki/PLD_Space#Miura_5,20.7 m
                        246,Molniya,https://en.wikipedia.org/wiki/Molniya_(rocket),43.4 m
                        247,Molniya-M /Block 2BL,https://en.wikipedia.org/wiki/Molniya-M,43.4 m
                        248,Molniya-M /Block L,https://en.wikipedia.org/wiki/Molniya-M,43.4 m
                        249,Molniya-M /Block ML,https://en.wikipedia.org/wiki/Molniya-M,43.4 m
                        250,Molniya-M /Block NVL,https://en.wikipedia.org/wiki/Molniya-M,43.4 m
                        251,Molniya-M /Block SO-L,https://en.wikipedia.org/wiki/Molniya-M,43.4 m
                        252,Molniya-M /Block VL,https://en.wikipedia.org/wiki/Molniya-M,43.4 m
                        253,MOMO,https://en.wikipedia.org/wiki/Interstellar_Technologies#MOMO_sounding_rocket,10.0 m
                        254,Mu-III C,https://en.wikipedia.org/wiki/Mu_(rocket_family),
                        255,Mu-III H,https://en.wikipedia.org/wiki/Mu_(rocket_family),
                        256,Mu-III S,https://en.wikipedia.org/wiki/Mu_(rocket_family),
                        257,Mu-III S2,https://en.wikipedia.org/wiki/Mu_(rocket_family),
                        258,Mu-IV S,https://en.wikipedia.org/wiki/Mu_(rocket_family),23.6 m
                        259,Mu-V / M-24,https://en.m.wikipedia.org/wiki/M-V,30.7 m
                        260,Mu-V / M-25,https://en.m.wikipedia.org/wiki/M-V,30.7 m
                        261,N1-L3,https://en.wikipedia.org/wiki/N1_(rocket),105.0 m
                        262,N1-L3 M,https://en.wikipedia.org/wiki/N1_(rocket),105.0 m
                        263,Naro-1,https://en.wikipedia.org/wiki/Naro-1,33.0 m
                        264,New Glenn,https://en.wikipedia.org/wiki/New_Glenn,82.0 m
                        265,New Shepard,https://en.wikipedia.org/wiki/New_Shepard,18.0 m
                        266,N-I,https://en.wikipedia.org/wiki/N-I_(rocket),34.0 m
                        267,N-II,https://en.wikipedia.org/wiki/N-II_(rocket),35.0 m
                        268,N-II Star-37E,https://en.wikipedia.org/wiki/N-II_(rocket),35.0 m
                        269,N-I Star-37E,https://en.wikipedia.org/wiki/N-I_(rocket),34.0 m
                        270,NOTS-EV-1 Pilot II,https://en.wikipedia.org/wiki/NOTS-EV-1_Pilot,4.0 m
                        271,NSL-A,https://gotospaceindustries.com/programmi-spaziali/lanciatori-orbitali/nanosatellite-launcher/,18.0 m
                        272,OS-M1,https://en.wikipedia.org/wiki/OneSpace#OS-M,
                        273,Palas 1,,42.0 m
                        274,Pegasus,https://en.wikipedia.org/wiki/Pegasus_(rocket),16.9 m
                        275,Pegasus/HAPS,https://en.wikipedia.org/wiki/Pegasus_(rocket),16.9 m
                        276,Pegasus XL,https://en.wikipedia.org/wiki/Pegasus_(rocket),17.6 m
                        277,Pegasus XL/HAPS,https://en.wikipedia.org/wiki/Pegasus_(rocket),17.6 m
                        278,Poliot,,
                        279,Proton,https://en.wikipedia.org/wiki/Proton_(rocket_family),39.8 m
                        280,Proton K,https://en.wikipedia.org/wiki/Proton-K,58.46 m
                        281,Proton K/Block D,https://en.wikipedia.org/wiki/Proton-K,56.14 m
                        282,Proton K/Block D-1,https://en.wikipedia.org/wiki/Proton-K,56.14 m
                        283,Proton K/Block D-2,https://en.wikipedia.org/wiki/Proton-K,56.14 m
                        284,Proton K/Block-DM,https://en.wikipedia.org/wiki/Proton-K,54.89 m
                        285,Proton K/Block DM-1,https://en.wikipedia.org/wiki/Proton-K,57.24 m
                        286,Proton K/Block DM2,https://en.wikipedia.org/wiki/Proton-K,57.64 m
                        287,Proton K/Block DM-2,https://en.wikipedia.org/wiki/Proton-K,57.64 m
                        288,Proton K/Block DM-2M,https://en.wikipedia.org/wiki/Proton-K,57.64 m
                        289,Proton K/Block DM-3,https://en.wikipedia.org/wiki/Proton-K,57.64 m
                        290,Proton K/Block DM-4,https://en.wikipedia.org/wiki/Proton-K,57.64 m
                        291,Proton K/Block DM-5,https://en.wikipedia.org/wiki/Proton-K,57.64 m
                        292,Proton K/Briz-M,https://en.wikipedia.org/wiki/Proton-K,54.54 m
                        293,Proton M,https://en.wikipedia.org/wiki/Proton-M,58.2 m
                        294,Proton-M/Briz-M,https://en.wikipedia.org/wiki/Proton-M,58.2 m
                        295,Proton-M/DM-2,https://en.wikipedia.org/wiki/Proton-M,53.0 m
                        296,Proton-M/DM-3,https://en.wikipedia.org/wiki/Proton-M,53.0 m
                        297,PSLV-CA,https://en.wikipedia.org/wiki/Polar_Satellite_Launch_Vehicle#Variants,44.0 m
                        298,PSLV-DL,https://en.wikipedia.org/wiki/Polar_Satellite_Launch_Vehicle,44.0 m
                        299,PSLV-G,https://en.wikipedia.org/wiki/Polar_Satellite_Launch_Vehicle#Variants,44.5 m
                        300,PSLV-QL,https://en.wikipedia.org/wiki/Polar_Satellite_Launch_Vehicle#Variants,44.0 m
                        301,PSLV-XL,https://en.wikipedia.org/wiki/Polar_Satellite_Launch_Vehicle#Variants,44.0 m
                        302,Qased,https://fr.wikipedia.org/wiki/Qased,18.6 m
                        303,Redstone Sparta,https://en.wikipedia.org/wiki/PGM-11_Redstone,21.0 m
                        304,Rocket 3,https://fr.wikipedia.org/wiki/Rocket_(fus%C3%A9e),11.6 m
                        305,Rokot/Briz K,https://en.wikipedia.org/wiki/Rokot,
                        306,Rokot/Briz KM,https://en.wikipedia.org/wiki/Rokot,29.0 m
                        307,Rokot-M/Briz KM-2,https://en.wikipedia.org/wiki/Rokot,29.0 m
                        308,Safir-1,https://en.wikipedia.org/wiki/Safir_(rocket),26.0 m
                        309,Safir-1A,https://en.wikipedia.org/wiki/Safir_(rocket),26.0 m
                        310,Safir-1B,https://en.wikipedia.org/wiki/Safir_(rocket),26.0 m
                        311,Safir-1B+,https://en.wikipedia.org/wiki/Safir_(rocket),26.0 m
                        312,SARGE,https://en.wikipedia.org/wiki/Exos_Aerospace#SARGE,11.0 m
                        313,Saturn I,https://en.wikipedia.org/wiki/Saturn_I,55.0 m
                        314,Saturn IB,https://en.wikipedia.org/wiki/Saturn_IB,43.2 m
                        315,Saturn V,https://en.wikipedia.org/wiki/Saturn_V,110.6 m
                        316,Scout B,https://en.wikipedia.org/wiki/Scout_(rocket_family),21.0 m
                        317,Scout B1,https://en.wikipedia.org/wiki/Scout_(rocket_family),21.0 m
                        318,Scout D1,https://en.wikipedia.org/wiki/Scout_(rocket_family),21.0 m
                        319,Scout F1,https://en.wikipedia.org/wiki/Scout_(rocket_family),21.0 m
                        320,Scout G1,https://en.wikipedia.org/wiki/Scout_(rocket_family),21.0 m
                        321,Scout X-1,https://en.wikipedia.org/wiki/Scout_X-1,25.0 m
                        322,Scout X-2,https://en.wikipedia.org/wiki/Scout_X-2,25.0 m
                        323,Scout X-2B,https://en.wikipedia.org/wiki/Scout_X-2B,22.0 m
                        324,Scout X-2M,https://en.wikipedia.org/wiki/Scout_X-2M,22.0 m
                        325,Scout X-3,,
                        326,Scout X-3M,,22.0 m
                        327,Scout X-4,,25.0 m
                        328,Shavit,https://en.wikipedia.org/wiki/Shavit,18.0 m
                        329,Shavit-1,https://en.wikipedia.org/wiki/Shavit,20.0 m
                        330,Shavit-2,https://en.wikipedia.org/wiki/Shavit,22.0 m
                        331,Shtil',https://en.wikipedia.org/wiki/Shtil%27,15.0 m
                        332,Simorgh,https://en.wikipedia.org/wiki/Simorgh_(rocket),27.0 m
                        333,SLS Block 1,https://en.wikipedia.org/wiki/Space_Launch_System,98.1 m
                        334,SLV-3,https://en.wikipedia.org/wiki/Satellite_Launch_Vehicle,22.0 m
                        335,SM-65B Atlas,https://en.wikipedia.org/wiki/SM-65B_Atlas,26.0 m
                        336,Soyuz,https://en.wikipedia.org/wiki/Soyuz_(rocket),45.6 m
                        337,Soyuz 2.1a,https://en.wikipedia.org/wiki/Soyuz-2,
                        338,Soyuz 2.1a/Fregat,https://en.wikipedia.org/wiki/Soyuz-2,42.5 m
                        339,Soyuz 2.1a/Fregat-M,https://en.wikipedia.org/wiki/Soyuz-2#Soyuz-2.1a,42.5 m
                        340,Soyuz 2.1a/Volga,https://en.wikipedia.org/wiki/Soyuz-2,
                        341,Soyuz 2.1b,https://en.wikipedia.org/wiki/Soyuz-2,
                        342,Soyuz 2.1b/Fregat,https://en.wikipedia.org/wiki/Soyuz-2,42.5 m
                        343,Soyuz 2.1b/Fregat-M,https://en.wikipedia.org/wiki/Soyuz-2,42.5 m
                        344,Soyuz 2.1v,https://en.wikipedia.org/wiki/Soyuz-2-1v,
                        345,Soyuz 2.1v/Volga,https://en.wikipedia.org/wiki/Soyuz-2-1v,
                        346,Soyuz 5,https://en.wikipedia.org/wiki/Irtysh_(rocket),61.9 m
                        347,Soyuz 5/Fregat SBU,https://en.wikipedia.org/wiki/Irtysh_(rocket),61.9 m
                        348,Soyuz 6,,
                        349,Soyuz 7,https://en.m.wikipedia.org/wiki/Soyuz-7_(rocket),
                        350,Soyuz FG,https://en.wikipedia.org/wiki/Soyuz-FG,49.5 m
                        351,Soyuz FG/Fregat,https://en.wikipedia.org/wiki/Soyuz-FG,42.5 m
                        352,Soyuz L,https://en.wikipedia.org/wiki/Soyuz-L,50.0 m
                        353,Soyuz M,https://en.wikipedia.org/wiki/Soyuz-M,50.0 m
                        354,Soyuz ST-A/Fregat,https://en.wikipedia.org/wiki/Soyuz-2,46.2 m
                        355,Soyuz ST-A/Fregat-M,https://en.wikipedia.org/wiki/Soyuz-2,46.2 m
                        356,Soyuz ST-B/Fregat-M,https://en.wikipedia.org/wiki/Soyuz-2#Soyuz-2.1b,46.2 m
                        357,Soyuz ST-B/Fregat-MT,https://en.wikipedia.org/wiki/Soyuz-2#Soyuz-2.1b,46.2 m
                        358,Soyuz U,https://en.wikipedia.org/wiki/Soyuz-U,51.1 m
                        359,Soyuz U2,https://en.wikipedia.org/wiki/Soyuz-U2,34.5 m
                        360,Soyuz U/Fregat,https://en.wikipedia.org/wiki/Soyuz-U,46.7 m
                        361,Soyuz U/Ikar,https://en.wikipedia.org/wiki/Soyuz-U,47.3 m
                        362,Space Shuttle Atlantis,https://en.wikipedia.org/wiki/Space_Shuttle_Atlantis,56.1 m
                        363,Space Shuttle Challenger,https://en.wikipedia.org/wiki/Space_Shuttle_Challenger,56.1 m
                        364,Space Shuttle Columbia,https://en.wikipedia.org/wiki/Space_Shuttle_Columbia,56.1 m
                        365,Space Shuttle Discovery,https://en.wikipedia.org/wiki/Space_Shuttle_Discovery,56.1 m
                        366,Space Shuttle Endeavour,https://en.wikipedia.org/wiki/Space_Shuttle_Endeavour,56.1 m
                        367,Sputnik 8A91,https://en.wikipedia.org/wiki/Sputnik_(rocket),31.0 m
                        368,Sputnik 8K71PS,https://en.wikipedia.org/wiki/Sputnik_(rocket),29.1 m
                        369,SS-520,https://en.wikipedia.org/wiki/S-Series_(rocket_family)#SS-520,9.54 m
                        370,SSLV,https://en.wikipedia.org/wiki/Small_Satellite_Launch_Vehicle,34.0 m
                        371,Starship Prototype,https://en.wikipedia.org/wiki/SpaceX_Starship,50.0 m
                        372,Starship-Super Heavy,https://en.wikipedia.org/wiki/BFR_(rocket),118.0 m
                        373,Start,https://en.wikipedia.org/wiki/Start-1#Variants,
                        374,Start-1,https://en.wikipedia.org/wiki/Start-1,23.0 m
                        375,Strela,https://en.wikipedia.org/wiki/Strela_(rocket),29.2 m
                        376,Super Stripy,https://en.wikipedia.org/wiki/SPARK_(rocket),18.0 m
                        377,Taepodong-1,https://en.wikipedia.org/wiki/Taepodong-1,26.0 m
                        378,Terran-1,https://en.wikipedia.org/wiki/Relativity_Space#Terran_1_launch_vehicle,
                        379,Thor-DM 18 Able I,https://en.wikipedia.org/wiki/Thor-Able,27.0 m
                        380,Thor DM-18 Able-II,https://en.wikipedia.org/wiki/Thor-Able,
                        381,Thor DM-18 Able-III,https://en.wikipedia.org/wiki/Thor-Able,
                        382,Thor DM-18 Able-IV,https://en.wikipedia.org/wiki/Thor-Able,
                        383,Thor-DM18 Agena-A,https://en.wikipedia.org/wiki/Thor-Agena,
                        384,Thor DM-19 Delta,https://en.wikipedia.org/wiki/Thor-Delta,
                        385,Thor-DM21 Ablestar,https://en.wikipedia.org/wiki/Thor-Ablestar,29.0 m
                        386,Thor DM-21 Agena-B,https://en.wikipedia.org/wiki/Thor-Agena,
                        387,Thor DM-21 Agena-D,https://en.wikipedia.org/wiki/Thor-Agena,
                        388,Thor-DSV2A Ablestar,https://en.wikipedia.org/wiki/Thor-Ablestar,
                        389,Thor-SLV2A Agena-D,https://en.wikipedia.org/wiki/Thor-Agena,31.0 m
                        390,Titan 34D,https://en.wikipedia.org/wiki/Titan_34D#Specification,50.0 m
                        391,Titan II(23)G,https://en.wikipedia.org/wiki/Titan_23G,42.9 m
                        392,Titan II GLV,https://en.wikipedia.org/wiki/Titan_II_GLV,33.2 m
                        393,Titan III(23)B,https://en.wikipedia.org/wiki/Titan_IIIB#Titan_23B,45.0 m
                        394,Titan III(23)C,https://en.wikipedia.org/wiki/Titan_IIIC,42.0 m
                        395,Titan III(24)B,https://en.wikipedia.org/wiki/Titan_IIIB#Titan_24B,50.0 m
                        396,Titan-III(33)B Agena-D,https://en.wikipedia.org/wiki/Titan_IIIB#Titan_33B,45.0 m
                        397,Titan III(34)B Agena-D,https://en.wikipedia.org/wiki/Titan_IIIB#Titan_34B,45.0 m
                        398,Titan IIIA,https://en.wikipedia.org/wiki/Titan_IIIA,42.0 m
                        399,Titan IIIB,https://en.wikipedia.org/wiki/Titan_IIIB,45.0 m
                        400,Titan IIIC,https://en.wikipedia.org/wiki/Titan_IIIC,42.0 m
                        401,Titan IIID,https://en.wikipedia.org/wiki/Titan_IIID,36.0 m
                        402,Titan IIIE,https://en.wikipedia.org/wiki/Titan_IIIE,48.8 m
                        403,Titan IV(401)A,https://en.wikipedia.org/wiki/Titan_IV#Titan_IV-A,63.0 m
                        404,Titan IV(401)B,https://en.wikipedia.org/wiki/Titan_IV#Titan_IV-B,62.0 m
                        405,Titan IV(402)A,https://en.wikipedia.org/wiki/Titan_IV#Titan_IV-A,54.0 m
                        406,Titan IV(402)B,https://en.wikipedia.org/wiki/Titan_IV#Titan_IV-B,54.0 m
                        407,Titan IV(403)A,https://en.wikipedia.org/wiki/Titan_IV#Titan_IV-A,54.0 m
                        408,Titan IV(403)B,https://en.wikipedia.org/wiki/Titan_IV#Titan_IV-B,54.0 m
                        409,Titan IV(404)A,https://en.wikipedia.org/wiki/Titan_IV#Titan_IV-A,54.0 m
                        410,Titan IV(404)B,https://en.wikipedia.org/wiki/Titan_IV#Titan_IV-B,54.0 m
                        411,Titan IV(405)A,https://en.wikipedia.org/wiki/Titan_IV#Titan_IV-A,54.0 m
                        412,Titan IV(405)B,https://en.wikipedia.org/wiki/Titan_IV#Titan_IV-B,54.0 m
                        413,Tsyklon,https://en.wikipedia.org/wiki/Tsyklon,40.0 m
                        414,Tsyklon-2,https://en.wikipedia.org/wiki/Tsyklon-2,
                        415,Tsyklon-2A,https://en.wikipedia.org/wiki/Tsyklon,""";
    }

    @BeforeAll
    static void setUpDataInMJTSpaceScanner() throws NoSuchAlgorithmException {
        // setting up secrete key and cipher to encrypt and decrypt
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ENCRYPTION_ALGORITHM);
        keyGenerator.init(KEY_SIZE_IN_BITS);
        secretKey = keyGenerator.generateKey();

        Reader misisonReader = new StringReader(missionsDataSet);
        Reader rocketReader = new StringReader(rocketDataSet);

        mjtSpaceScanner = new MJTSpaceScanner(misisonReader, rocketReader, secretKey);
    }

    // public Collection<Mission> getAllMissions()
    @Test
    void testGetAllMissionsSuccessfully() {
        BufferedReader missionReader = new BufferedReader(new StringReader(missionsDataSet));
        List<Mission> missionList = missionReader.lines().skip(SKIP_LEGEND_LINE).map(Mission::of).toList();
        List<Mission> allMissionsFromSpaceRunner = new ArrayList<>(mjtSpaceScanner.getAllMissions());

        assertEquals(allMissionsFromSpaceRunner.size(), missionList.size(),
                "The size of the same collections have to be the same");
        for (int i = 0; i < missionList.size(); i++) {
            assertEquals(missionList.get(i), allMissionsFromSpaceRunner.get(i),
                    "There is a missmatch between the read missions: \n" +
                            missionList.get(i).toString() + "\n" +
                            allMissionsFromSpaceRunner.get(i).toString());
        }
    }

    // public Collection<Mission> getAllMissions(MissionStatus missionStatus)
    @Test
    void testNullArgumentThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> mjtSpaceScanner.getAllMissions(null),
                "When null is passed as an argument to getAllMissions, IllegalArgumentException is thrown");
    }

    @Test
    void testGetAllSuccessMissions() {
        BufferedReader missionReader = new BufferedReader(new StringReader(missionsDataSet));
        List<Mission> allSuccessfulMissions = missionReader.lines()
                .skip(SKIP_LEGEND_LINE)
                .map(Mission::of)
                .filter(mission -> mission.missionStatus() == MissionStatus.SUCCESS)
                .toList();

        List<Mission> allSuccessfulMissionsFromSpaceRunner =
                new ArrayList<>(mjtSpaceScanner.getAllMissions(MissionStatus.SUCCESS));

        assertEquals(allSuccessfulMissionsFromSpaceRunner.size(), allSuccessfulMissions.size(),
                "The size of the same collections have to be the same");
        for (int i = 0; i < allSuccessfulMissions.size(); i++) {
            assertEquals(allSuccessfulMissions.get(i), allSuccessfulMissionsFromSpaceRunner.get(i),
                    "There is a missmatch between the read missions: \n" +
                            allSuccessfulMissions.get(i).toString() + "\n" +
                            allSuccessfulMissionsFromSpaceRunner.get(i).toString());
        }
    }

    @Test
    void testGetAllFailureMissions() {
        BufferedReader missionReader = new BufferedReader(new StringReader(missionsDataSet));
        List<Mission> allSuccessfulMissions = missionReader.lines()
                .skip(SKIP_LEGEND_LINE)
                .map(Mission::of)
                .filter(mission -> mission.missionStatus() == MissionStatus.FAILURE)
                .toList();

        List<Mission> allSuccessfulMissionsFromSpaceRunner =
                new ArrayList<>(mjtSpaceScanner.getAllMissions(MissionStatus.FAILURE));

        assertEquals(allSuccessfulMissionsFromSpaceRunner.size(), allSuccessfulMissions.size(),
                "The size of the same collections have to be the same");
        for (int i = 0; i < allSuccessfulMissions.size(); i++) {
            assertEquals(allSuccessfulMissions.get(i), allSuccessfulMissionsFromSpaceRunner.get(i),
                    "There is a missmatch between the read missions: \n" +
                            allSuccessfulMissions.get(i).toString() + "\n" +
                            allSuccessfulMissionsFromSpaceRunner.get(i).toString());
        }
    }

    @Test
    void testGetAllPartialFailureMissions() {
        BufferedReader missionReader = new BufferedReader(new StringReader(missionsDataSet));
        List<Mission> allSuccessfulMissions = missionReader.lines()
                .skip(SKIP_LEGEND_LINE)
                .map(Mission::of)
                .filter(mission -> mission.missionStatus() == MissionStatus.PARTIAL_FAILURE)
                .toList();

        List<Mission> allSuccessfulMissionsFromSpaceRunner =
                new ArrayList<>(mjtSpaceScanner.getAllMissions(MissionStatus.PARTIAL_FAILURE));

        assertEquals(allSuccessfulMissionsFromSpaceRunner.size(), allSuccessfulMissions.size(),
                "The size of the same collections have to be the same");
        for (int i = 0; i < allSuccessfulMissions.size(); i++) {
            assertEquals(allSuccessfulMissions.get(i), allSuccessfulMissionsFromSpaceRunner.get(i),
                    "There is a missmatch between the read missions: \n" +
                            allSuccessfulMissions.get(i).toString() + "\n" +
                            allSuccessfulMissionsFromSpaceRunner.get(i).toString());
        }
    }

    @Test
    void testGetAllPreLaunchFailureMissions() {
        BufferedReader missionReader = new BufferedReader(new StringReader(missionsDataSet));
        List<Mission> allSuccessfulMissions = missionReader.lines()
                .skip(SKIP_LEGEND_LINE)
                .map(Mission::of)
                .filter(mission -> mission.missionStatus() == MissionStatus.PRELAUNCH_FAILURE)
                .toList();

        List<Mission> allSuccessfulMissionsFromSpaceRunner =
                new ArrayList<>(mjtSpaceScanner.getAllMissions(MissionStatus.PRELAUNCH_FAILURE));

        assertEquals(allSuccessfulMissionsFromSpaceRunner.size(), allSuccessfulMissions.size(),
                "The size of the same collections have to be the same");
        for (int i = 0; i < allSuccessfulMissions.size(); i++) {
            assertEquals(allSuccessfulMissions.get(i), allSuccessfulMissionsFromSpaceRunner.get(i),
                    "There is a missmatch between the read missions: \n" +
                            allSuccessfulMissions.get(i).toString() + "\n" +
                            allSuccessfulMissionsFromSpaceRunner.get(i).toString());
        }
    }

    // public String getCompanyWithMostSuccessfulMissions(LocalDate from, LocalDate to)
    @Test
    void testNullFromDateArgumentCompanyWithMostSuccessfulMissionsThrowException() {
        LocalDate toDate = LocalDate.parse("Mon Jul 20, 2020", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));
        assertThrows(IllegalArgumentException.class,
                () -> mjtSpaceScanner.getCompanyWithMostSuccessfulMissions(null, toDate),
                "When from time period is null IllegalArgumentException is thrown");
    }

    @Test
    void testNullToDateArgumentCompanyWithMostSuccessfulMissionsThrowException() {
        LocalDate fromDate = LocalDate.parse("Mon Jul 20, 2020", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));
        assertThrows(IllegalArgumentException.class,
                () -> mjtSpaceScanner.getCompanyWithMostSuccessfulMissions(fromDate, null),
                "When to time period is null IllegalArgumentException is thrown");
    }

    @Test
    void testBothDatesArgumentsComapnyWithMostSuccessfulMissionsThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> mjtSpaceScanner.getCompanyWithMostSuccessfulMissions(null, null),
                "When both of the dates arguments are null IllegalArgumentException shall be thrown");
    }

    @Test
    void testToDateIsBeforeFromDateThrowsExceptionForCompanyWithMostSuccessfulMissions() {
        LocalDate fromDate = LocalDate.parse("Mon Jul 20, 2020", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));
        LocalDate toDate = LocalDate.parse("Sun Jul 19, 2020", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));
        assertThrows(TimeFrameMismatchException.class,
                () -> mjtSpaceScanner.getCompanyWithMostSuccessfulMissions(fromDate, toDate),
                "When to date is before from date it should throw TimeFrameMismatchException");
    }

    @Test
    void testToDateIsBeforeFromDateThrowsExceptionForCompanyWithMostSuccessfulMissions_WithThrowable() {
        MJTSpaceScanner tempMjtSpaceScanner = mock(MJTSpaceScanner.class);

        LocalDate toDate = LocalDate.parse("Mon Jul 20, 2020", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));
        LocalDate fromDate = LocalDate.parse("Sun Jul 19, 2020", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));
        doThrow(new TimeFrameMismatchException("To is before from", new Throwable()))
                .when(tempMjtSpaceScanner).getCompanyWithMostSuccessfulMissions(toDate, fromDate);

        assertThrows(TimeFrameMismatchException.class, () -> mjtSpaceScanner.getCompanyWithMostSuccessfulMissions(toDate, fromDate),
                "When to date is before from date it should throw TimeFrameMismatchException");
    }

    @Test
    void testProperTimeFramesReturnsCompanyWithMostSuccessfulMission() {
        LocalDate fromDate = LocalDate.parse("Wed Dec 11, 2019", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));
        LocalDate toDate = LocalDate.parse("Tue Aug 04, 2020", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));

        String expectedCompany = "CASC";
        String companyWithMostSuccessfulMission =
                mjtSpaceScanner.getCompanyWithMostSuccessfulMissions(fromDate, toDate);

        assertEquals(expectedCompany, companyWithMostSuccessfulMission,
                "The company with the most successful mission is CASC");
    }

    @Test
    void testProperTimeFramesButNoRocketInThisFrameReturnsEmptyString() {
        LocalDate fromDate = LocalDate.parse("Sat Aug 08, 2020", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));
        LocalDate toDate = LocalDate.parse("Mon Aug 10, 2020", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));

        String companyWithMostSuccessfulMission =
                mjtSpaceScanner.getCompanyWithMostSuccessfulMissions(fromDate, toDate);

        assertEquals("", companyWithMostSuccessfulMission,
                "No rockets in this period, empty string is returned");
    }

    // public Map<String, Collection<Mission>> getMissionsPerCountry()
    @Test
    void testNoLoaddedMissionsReturnEmptyMap() {
        MJTSpaceScanner tempMJTSpaceScanner = new MJTSpaceScanner(
                new StringReader(""),
                new StringReader(""),
                null);
        assertEquals(new HashMap<>(), tempMJTSpaceScanner.getMissionsPerCountry(),
                "The returned hashmap shall be empty");
    }

    @Test
    void testSuccessfulMissionsPerCountry() {
        Function<String[], String> formatCountry =
                locationArgs -> locationArgs[locationArgs.length - GET_COUNTRY_ARG];

        // maps every country to have a list of all the missions that happened in it
        BufferedReader missionReader = new BufferedReader(new StringReader(missionsDataSet));
        Map<String, Collection<Mission>> missionsPerCountryDataset = missionReader.lines()
                .skip(SKIP_LEGEND_LINE)
                .map(Mission::of)
                .collect(Collectors.groupingBy(
                        mission -> formatCountry.apply(mission.location().split(",")).trim(),
                        Collectors.toCollection(ArrayList::new)
                ));

        Map<String, Collection<Mission>> missionsPerCountrySpaceRunner = mjtSpaceScanner.getMissionsPerCountry();

        assertEquals(missionsPerCountryDataset.size(), missionsPerCountrySpaceRunner.size(),
                "The two maps differ in size");

        // compares the information gotten from the dataset with the information given from the mjtSpaceScanner
        Iterator<Map.Entry<String, Collection<Mission>>> iteratorDataset =
                missionsPerCountryDataset.entrySet().iterator();
        Iterator<Map.Entry<String, Collection<Mission>>> iteratorSpaceScanner =
                missionsPerCountryDataset.entrySet().iterator();
        while (iteratorSpaceScanner.hasNext() && iteratorDataset.hasNext()) {

            Map.Entry<String, Collection<Mission>> entryDataSet = iteratorDataset.next();
            Map.Entry<String, Collection<Mission>> entrySpaceScanner = iteratorSpaceScanner.next();

            assertEquals(entryDataSet.getKey(),
                    entrySpaceScanner.getKey(),
                    "The keys are different [" + entryDataSet.getKey() + "]" +
                            " and " + "[" + entrySpaceScanner.getKey() + "]");

            assertEquals(entryDataSet.getValue().size(), entrySpaceScanner.getValue().size(),
                    "There is a missmatch between the size of the collections for keys " +
                            entryDataSet.getKey() + "]" + " and " + "[" + entrySpaceScanner.getKey() + "]");

            List<Mission> listDataSet = new ArrayList<>(entryDataSet.getValue());
            List<Mission> listSpaceScanner = new ArrayList<>(entrySpaceScanner.getValue());

            for (int i = 0; i < entryDataSet.getValue().size(); i++) {
                assertEquals(listDataSet.get(i), listSpaceScanner.get(i),
                        "There is a mismatch between the values " +
                                listDataSet.get(i) + "\n" + listSpaceScanner.get(i));
            }
        }
    }

    // public List<Mission> getTopNLeastExpensiveMissions(int n, MissionStatus missionStatus, RocketStatus rocketStatus)
    @Test
    void testGetTopNLeastExpensiveMissionsWhenNIs0ThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> mjtSpaceScanner.getTopNLeastExpensiveMissions(
                        0,
                        MissionStatus.SUCCESS,
                        RocketStatus.STATUS_ACTIVE),
                "When n paremeter is 0 for getTopNLeastExpensiveMissions IllegalArgumentException is thrown");
    }

    @Test
    void testGetTopNLeastExpensiveMissionsWhenNIsBelow0ThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> mjtSpaceScanner.getTopNLeastExpensiveMissions(
                        INVALID_ARG,
                        MissionStatus.SUCCESS,
                        RocketStatus.STATUS_ACTIVE),
                "When n paremeter is below 0 for getTopNLeastExpensiveMissions" +
                        " IllegalArgumentException is thrown");
    }

    @Test
    void testGetTopNLeastExpensiveMissionsWhenMissionStatusNullThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> mjtSpaceScanner.getTopNLeastExpensiveMissions(
                        TOP_FIVE,
                        null,
                        RocketStatus.STATUS_ACTIVE),
                "When mission status is null for getTopNLeastExpensiveMissions" +
                        " IllegalArgumentException is thrown");
    }

    @Test
    void testGetTopNLeastExpensiveMissionsWhenRocketStatusNullThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> mjtSpaceScanner.getTopNLeastExpensiveMissions(
                        TOP_FIVE,
                        MissionStatus.SUCCESS,
                        null),
                "When rocket status is null for getTopNLeastExpensiveMissions" +
                        " IllegalArgumentException is thrown");
    }

    @Test
    void getTop10LeastExpensiveSuccessfulMissions() {
        BufferedReader missionReader = new BufferedReader(new StringReader(missionsDataSet));

        // getting the actual top 10 least expensive from the data set
        List<Mission> fromDataSet = missionReader.lines()
                .skip(SKIP_LEGEND_LINE)
                .map(Mission::of)
                .filter(mission -> mission.missionStatus() == MissionStatus.SUCCESS
                        && mission.rocketStatus() == RocketStatus.STATUS_ACTIVE)
                .filter(mission -> mission.cost().isPresent())
                .sorted(Comparator.comparingDouble(mission -> mission.cost().get()))
                .limit(TOP_TEN)
                .toList();

        // getting from the main software
        List<Mission> fromSpaceRunner =
                mjtSpaceScanner.getTopNLeastExpensiveMissions(
                        TOP_TEN,
                        MissionStatus.SUCCESS,
                        RocketStatus.STATUS_ACTIVE);

        assertEquals(fromDataSet.size(), fromSpaceRunner.size(),
                "There is a mismatch between the size of the function");
        for (int i = 0; i < fromDataSet.size(); i++) {
            assertEquals(fromDataSet.get(i), fromSpaceRunner.get(i),
                    "There is a difference between those two elements \n" +
                            fromDataSet + "\n" + fromSpaceRunner);
        }
    }

    @Test
    void getTop20LeastExpensiveSuccessfulMissions() {
        BufferedReader missionReader = new BufferedReader(new StringReader(missionsDataSet));

        // getting the actual top 10 least expensive from the data set
        List<Mission> fromDataSet = missionReader.lines()
                .skip(SKIP_LEGEND_LINE)
                .map(Mission::of)
                .filter(mission -> mission.missionStatus() == MissionStatus.SUCCESS
                        && mission.rocketStatus() == RocketStatus.STATUS_ACTIVE)
                .filter(mission -> mission.cost().isPresent())
                .sorted(Comparator.comparingDouble(mission -> mission.cost().get()))
                .limit(TOP_TWENTY)
                .toList();

        // getting from the main software
        List<Mission> fromSpaceRunner =
                mjtSpaceScanner.getTopNLeastExpensiveMissions(
                        TOP_TWENTY,
                        MissionStatus.SUCCESS,
                        RocketStatus.STATUS_ACTIVE);

        assertEquals(fromDataSet.size(), fromSpaceRunner.size(),
                "There is a mismatch between the size of the function");
        for (int i = 0; i < fromDataSet.size(); i++) {
            assertEquals(fromDataSet.get(i), fromSpaceRunner.get(i),
                    "There is a difference between those two elements \n" +
                            fromDataSet + "\n" + fromSpaceRunner);
        }
    }

    @Test
    void testNoMissionsReturnsEmptyListForLeastExpensiveMissions() {
        MJTSpaceScanner tempMJTSpaceScanner = new MJTSpaceScanner(
                new StringReader(""),
                new StringReader(""),
                null);
        assertEquals(List.of(),
                tempMJTSpaceScanner.getTopNLeastExpensiveMissions(
                        TOP_TEN,
                        MissionStatus.SUCCESS,
                        RocketStatus.STATUS_ACTIVE),
                "When there are no missions empty list shall be returned");
    }

    // public Map<String, String> getMostDesiredLocationForMissionsPerCompany()
    @Test
    void testReturnMostDesiredLocationForTestingByEachCompany() {
        BufferedReader misisonsReader = new BufferedReader(new StringReader(missionsDataSet));
        List<Mission> missionsDataSet = misisonsReader.lines().skip(SKIP_LEGEND_LINE)
                .map(Mission::of).toList();

        // for each company, count how many missions where on a location
        Map<String, Map<String, Long>> missionsPerCompany = missionsDataSet.stream()
                .collect(Collectors.groupingBy(
                        Mission::company,
                        Collectors.groupingBy(Mission::location, Collectors.counting())
                ));
        // for each company, compare which is the most desired location
        Map<String, String> topLocationDataSet = missionsPerCompany.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().entrySet().stream()
                                .max(Map.Entry.comparingByValue())
                                .map(Map.Entry::getKey)
                                .orElse("")
                ));
        Map<String, String> topLocationSpaceScanner = mjtSpaceScanner.getMostDesiredLocationForMissionsPerCompany();


        assertEquals(topLocationDataSet.size(), topLocationSpaceScanner.size(),
                "The two maps dont have equal size");

        Iterator<Map.Entry<String, String>> iteratorDataSet = topLocationDataSet.entrySet().iterator();
        Iterator<Map.Entry<String, String>> iteratorSpaceScanner = topLocationSpaceScanner.entrySet().iterator();
        while (iteratorSpaceScanner.hasNext() && iteratorDataSet.hasNext()) {

            Map.Entry<String, String> entryDataSet = iteratorDataSet.next();
            Map.Entry<String, String> entrySpaceScanner = iteratorSpaceScanner.next();

            assertEquals(entryDataSet.getKey(), entrySpaceScanner.getKey(),
                    "The keys are different [" + entryDataSet.getKey() + "]" +
                            " and " + "[" + entrySpaceScanner.getKey() + "]");
            assertEquals(entryDataSet.getValue(), entrySpaceScanner.getValue(),
                    "The values are different [" + entryDataSet.getValue() + "]" +
                            " and " + "[" + entrySpaceScanner.getValue() + "]");
        }
    }

    @Test
    void testNoMissionsReturnsEmptyMapForLeastExpensiveMissions() {
        MJTSpaceScanner tempMJTSpaceScanner = new MJTSpaceScanner(
                new StringReader(""),
                new StringReader(""),
                null);
        assertEquals(new HashMap<>(),
                tempMJTSpaceScanner.getMostDesiredLocationForMissionsPerCompany(),
                "When there are no missions empty list shall be returned");
    }

    // public Map<String, String> getLocationWithMostSuccessfulMissionsPerCompany(LocalDate from, LocalDate to)
    @Test
    void testNullArgumentsForLocWithMostSuccessfulMissionsPerCompanyThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> mjtSpaceScanner.getLocationWithMostSuccessfulMissionsPerCompany(null, null),
                "When the both arguments are with null value IllegalArgumentException shall be thrown");
    }

    @Test
    void tesFromDateIsNullThrowsException() {
        //LocalDate fromDate = LocalDate.parse("Wed Oct 09, 2019", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));
        LocalDate toDate = LocalDate.parse("Mon Aug 10, 2020", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));
        assertThrows(IllegalArgumentException.class,
                () -> mjtSpaceScanner.getLocationWithMostSuccessfulMissionsPerCompany(null, toDate),
                "From date cannot be null");
    }

    @Test
    void tesToDateIsNullThrowsException() {
        LocalDate fromDate = LocalDate.parse("Wed Oct 09, 2019", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));
        //LocalDate toDate = LocalDate.parse("Mon Aug 10, 2020", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));
        assertThrows(IllegalArgumentException.class,
                () -> mjtSpaceScanner.getLocationWithMostSuccessfulMissionsPerCompany(fromDate, null),
                "to date cannot be null");
    }

    @Test
    void testToBeforeFromThrowsException() {
        LocalDate toDate = LocalDate.parse("Wed Oct 09, 2019", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));
        LocalDate fromDate = LocalDate.parse("Mon Aug 10, 2020", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));
        assertThrows(TimeFrameMismatchException.class,
                () -> mjtSpaceScanner.getLocationWithMostSuccessfulMissionsPerCompany(fromDate, toDate),
                "to date cannot be before from date");
    }

    @Test
    void testNoMissionsReturnsEmptyMapForMostSuccessfulMissionsPerCompany() {
        MJTSpaceScanner tempMJTSpaceScanner = new MJTSpaceScanner(
                new StringReader(""),
                new StringReader(""),
                null);

        LocalDate fromDate = LocalDate.parse("Wed Oct 09, 2019", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));
        LocalDate toDate = LocalDate.parse("Mon Aug 10, 2020", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));
        assertEquals(new HashMap<>(),
                tempMJTSpaceScanner.getLocationWithMostSuccessfulMissionsPerCompany(fromDate, toDate),
                "When there are no missions empty map shall be returned");
    }

    @Test
    void testSuccessfulMapWithLocationPerCompanyWithMostSuccessfulMissions() {
        BufferedReader misisonsReader = new BufferedReader(new StringReader(missionsDataSet));
        List<Mission> missionsDataSet = misisonsReader.lines().skip(SKIP_LEGEND_LINE)
                .map(Mission::of).toList();
        LocalDate from = LocalDate.parse("Wed Oct 09, 2019", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));
        LocalDate to = LocalDate.parse("Fri Aug 07, 2020", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));

        // predicate that checks is a mission in the time period
        Predicate<Mission> isMissionInPeriod = mission ->
                ((mission.date().equals(from) || mission.date().isAfter(from)) &&
                        (mission.date().isBefore(to) || (mission.date().equals(to))));
        // for each company, count the times for each location that had missions in [from,to] which were successful
        Map<String, Map<String, Long>> successfulMissionsPerCompany = missionsDataSet.stream()
                .filter(isMissionInPeriod)
                .filter(mission -> mission.missionStatus() == MissionStatus.SUCCESS)
                .collect(
                        Collectors.groupingBy(
                                Mission::company,
                                Collectors.groupingBy(
                                        Mission::location,
                                        Collectors.counting()
                                )
                        )
                );

        // for each company assign the location with the most succ missions
        Map<String, String> mostSuccessLocForCompanyDataSet = successfulMissionsPerCompany.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().entrySet().stream()
                                .max(Map.Entry.comparingByValue())
                                .map(Map.Entry::getKey)
                                .orElse("")
                ));

        Map<String, String> mostSuccessLocForCompanySpaceScanner = mjtSpaceScanner.getLocationWithMostSuccessfulMissionsPerCompany(from, to);

        assertEquals(mostSuccessLocForCompanyDataSet.size(),
                mostSuccessLocForCompanySpaceScanner.size(),
                "The two maps dont have equal size");

        Iterator<Map.Entry<String, String>> iteratorDataSet = mostSuccessLocForCompanyDataSet.entrySet().iterator();
        Iterator<Map.Entry<String, String>> iteratorSpaceScanner =
                mostSuccessLocForCompanySpaceScanner.entrySet().iterator();

        while (iteratorSpaceScanner.hasNext() && iteratorDataSet.hasNext()) {

            Map.Entry<String, String> entryDataSet = iteratorDataSet.next();
            Map.Entry<String, String> entrySpaceScanner = iteratorSpaceScanner.next();

            assertEquals(entryDataSet.getKey(), entrySpaceScanner.getKey(),
                    "The keys are different [" + entryDataSet.getKey() + "]" +
                            " and " + "[" + entrySpaceScanner.getKey() + "]");
            assertEquals(entryDataSet.getValue(), entrySpaceScanner.getValue(),
                    "The values are different [" + entryDataSet.getValue() + "]" +
                            " and " + "[" + entrySpaceScanner.getValue() + "]");
        }
    }

    // public Collection<Rocket> getAllRockets()
    @Test
    void testNoRocketsReturnsEmptyCollection() {
        BufferedReader rocketsReader = new BufferedReader(new StringReader(""));
        Collection<Rocket> rocketDataSet = rocketsReader.lines().map(Rocket::of).toList();

        assertEquals(0, rocketDataSet.size(),
                "When the string is empty, the collection shall be empty");
    }

    @Test
    void testSuccessfulGettingRocketData() {
        BufferedReader rocketsReader = new BufferedReader(new StringReader(rocketDataSet));
        List<Rocket> rocketDataSet = rocketsReader.lines().skip(SKIP_LEGEND_LINE).map(Rocket::of).toList();
        List<Rocket> rocketSpaceRunner = (List<Rocket>) mjtSpaceScanner.getAllRockets();

        assertEquals(rocketDataSet.size(), rocketSpaceRunner.size(),
                "The size differes between the collections");
        for (int i = 0; i < rocketDataSet.size(); i++) {
            assertEquals(rocketDataSet.get(i), rocketSpaceRunner.get(i),
                    "There is a mismatch between those two rockets: \n" +
                            rocketDataSet.get(i) + " \n" + rocketSpaceRunner.get(i));
        }
    }

    // public List<Rocket> getTopNTallestRockets(int n)
    @Test
    void testNIs0ThrowsExceptionWhenGettingTopNTallestRockets() {
        assertThrows(IllegalArgumentException.class,
                () -> mjtSpaceScanner.getTopNTallestRockets(0),
                "When 0 is passed as argument for getTopNTaleestRockets throw IllegalArgumentException");
    }

    @Test
    void testNIsBelow0ThrowsExceptionWhenGettingTopNTallestRockets() {
        assertThrows(IllegalArgumentException.class,
                () -> mjtSpaceScanner.getTopNTallestRockets(INVALID_ARG),
                "When a value below 0 is passed as argument" +
                        " for getTopNTaleestRockets throw IllegalArgumentException");
    }

    @Test
    void testNoRocketsLoadedReturnsEmptyListWhenGettingTopNTallestRockets() {
        MJTSpaceScanner tempMJTSpaceScanner = new MJTSpaceScanner(
                new StringReader(""),
                new StringReader(""),
                null);
        assertEquals(List.of(), tempMJTSpaceScanner.getTopNTallestRockets(TOP_TEN),
                "Expected empty list when there are not any loaded rockets");
    }

    @Test
    void testGetTop10MostTallestRockets() {
        BufferedReader rocketReader = new BufferedReader(new StringReader(rocketDataSet));
        List<Rocket> rocketDataset = rocketReader.lines()
                .skip(SKIP_LEGEND_LINE)
                .map(Rocket::of)
                .filter(rocket -> rocket.height().isPresent())
                .sorted(Comparator.comparingDouble((Rocket rocket) -> rocket.height().get()).reversed())
                .limit(TOP_TEN)
                .toList();

        List<Rocket> rocketSpaceScanner = mjtSpaceScanner.getTopNTallestRockets(TOP_TEN);

        assertEquals(rocketDataset.size(), rocketSpaceScanner.size(), "There is a mismatch between the size");
        for (int i = 0; i < rocketDataset.size(); i++) {
            assertEquals(rocketDataset.get(i), rocketSpaceScanner.get(i),
                    "There is a missmatch between the elements: \n" +
                            rocketDataset + "\n" + rocketSpaceScanner);
        }
    }

    // public Map<String, Optional<String>> getWikiPageForRocket()
    @Test
    void testNoRocketsLoadedWillResultEmptyMap() {
        MJTSpaceScanner tempMJTSpaceScanner = new MJTSpaceScanner(
                new StringReader(""),
                new StringReader(""),
                null);
        assertEquals(new HashMap<>(), tempMJTSpaceScanner.getWikiPageForRocket(),
                "When there are no loaded rockets, empty hashmap is expected");
    }

    @Test
    void testMapWithRocketsNameAndTheirWikiSuccessful() {
        BufferedReader bufferedReader = new BufferedReader(new StringReader(rocketDataSet));
        Map<String, Optional<String>> wikiPerRocketName = bufferedReader.lines().skip(SKIP_LEGEND_LINE)
                .map(Rocket::of)
                .filter(rocket -> rocket.wiki().isPresent())
                .collect(Collectors.toMap(
                        Rocket::name,
                        Rocket::wiki
                ));

        Map<String, Optional<String>> wikiPerRocketNameSpaceScanner = mjtSpaceScanner.getWikiPageForRocket();

        assertEquals(wikiPerRocketName.size(), wikiPerRocketNameSpaceScanner.size(),
                "There is a difference between the sizes");

        Iterator<Map.Entry<String, Optional<String>>> iteratorDataset = wikiPerRocketName.entrySet().iterator();
        Iterator<Map.Entry<String, Optional<String>>> iteratorSpaceScanner =
                wikiPerRocketNameSpaceScanner.entrySet().iterator();

        while (iteratorDataset.hasNext() && iteratorSpaceScanner.hasNext()) {

            Map.Entry<String, Optional<String>> entryDataset = iteratorDataset.next();
            Map.Entry<String, Optional<String>> entrySpaceScanner = iteratorSpaceScanner.next();

            assertEquals(entryDataset.getKey(), entrySpaceScanner.getKey(),
                    "There is a difference between the keys: " +
                            "\n" + entryDataset.getKey() + "\n" + entrySpaceScanner.getKey());

            assertTrue(entrySpaceScanner.getValue().isPresent(),
                    "The value shall not be Optional.empty() for key " + entrySpaceScanner.getKey());
            assertTrue(entryDataset.getValue().isPresent(),
                    "The value shall not be Optional.empty() for key " + entryDataset.getKey());

            assertEquals(entryDataset.getValue().get(), entrySpaceScanner.getValue().get(),
                    "There is a difference between the values: " +
                            "\n" + entryDataset.getValue().get() + "\n" + entrySpaceScanner.getValue().get());
        }
    }

    // public List<String> getWikiPagesForRocketsUsedInMostExpensiveMissions(int n, MissionStatus missionStatus,
    //                                                                          RocketStatus rocketStatus)
    @Test
    void testWikiPagesForRocketsInExpensiveMissionThrowsExceptionWhenNIs0() {
        assertThrows(IllegalArgumentException.class,
                () -> mjtSpaceScanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(0,
                        MissionStatus.SUCCESS,
                        RocketStatus.STATUS_RETIRED));
    }

    @Test
    void testWikiPagesForRocketsInExpensiveMissionThrowsExceptionWhenNIsBelow0() {
        assertThrows(IllegalArgumentException.class,
                () -> mjtSpaceScanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(INVALID_ARG,
                        MissionStatus.SUCCESS,
                        RocketStatus.STATUS_ACTIVE));
    }

    @Test
    void testWikiPagesForRocketsInExpensiveMissionThrowsExceptionWhenMissionStatusNull() {
        assertThrows(IllegalArgumentException.class,
                () -> mjtSpaceScanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(
                        TOP_FIVE,
                        null,
                        RocketStatus.STATUS_ACTIVE));
    }

    @Test
    void testWikiPagesForRocketsInExpensiveMissionThrowsExceptionWhenRocketStatusNull() {
        assertThrows(IllegalArgumentException.class,
                () -> mjtSpaceScanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(
                        TOP_FIVE,
                        MissionStatus.SUCCESS,
                        null));
    }

    @Test
    void testWikiPagesForRocketsInExpensiveMissionReturnsEmptyListWhenNoMissions() {
        MJTSpaceScanner tempMjtSpaceScanner = new MJTSpaceScanner(
                new StringReader(""),
                new StringReader(""),
                null);
        assertEquals(List.of(), tempMjtSpaceScanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(
                TOP_TEN,
                MissionStatus.SUCCESS,
                RocketStatus.STATUS_ACTIVE));
    }

    @Test
    void testWikiPagesForRocketsInExpensiveMissionsReturnProperList() {
        BufferedReader readerMission = new BufferedReader(new StringReader(missionsDataSet));
        BufferedReader readerRocket = new BufferedReader(new StringReader(rocketDataSet));

        List<Mission> missionList = readerMission.lines().skip(SKIP_LEGEND_LINE).map(Mission::of).toList();
        List<Rocket> rocketList = readerRocket.lines().skip(SKIP_LEGEND_LINE).map(Rocket::of).toList();

        List<String> mostExpensiveMissions = missionList.stream()
                // all the missions with missions status and rocket status passed as arguments
                .filter(mission -> mission.missionStatus() == MissionStatus.SUCCESS
                        && mission.rocketStatus() == RocketStatus.STATUS_ACTIVE)
                .filter(mission -> mission.cost().isPresent())
                // sorting the missions based on their cost, the expensive are first
                .sorted(Comparator.comparingDouble((Mission mission) -> mission.cost().get()).reversed())
                // getting the top 10
                .limit(TOP_TEN)
                // getting from the missions the rocket name
                .map(mission -> mission.detail().rocketName())
                .toList();

        List<String> wikiPagesDataset = rocketList.stream()
                .filter(rocket -> rocket.wiki().isPresent())
                // filters to all the rockets that were used in the most n expensive missions
                .filter(rocket -> mostExpensiveMissions.contains(rocket.name()))
                // changes the rocket names to their wiki
                .map(rocket -> rocket.wiki().orElse(""))
                .toList();

        List<String> wikiPagesSpaceScanner = mjtSpaceScanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(
                TOP_TEN,
                MissionStatus.SUCCESS,
                RocketStatus.STATUS_ACTIVE);

        assertEquals(wikiPagesDataset.size(), wikiPagesSpaceScanner.size(), "the two list differ in size");
        for (int i = 0; i < wikiPagesDataset.size(); i++) {
            assertEquals(wikiPagesDataset.get(i), wikiPagesSpaceScanner.get(i), "There is a mismatch between " +
                    wikiPagesDataset.get(i) + " " + wikiPagesSpaceScanner.get(i)
            );
        }
    }

    // public void saveMostReliableRocket(OutputStream outputStream, LocalDate from, LocalDate to)
    // throws CipherException
    @Test
    void testMostReliableRocketThrowsExceptionWhenOutputStreamNull() {
        LocalDate fromDate = LocalDate.parse("Wed Oct 09, 2019", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));
        LocalDate toDate = LocalDate.parse("Mon Aug 10, 2020", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));
        assertThrows(IllegalArgumentException.class, () -> mjtSpaceScanner.saveMostReliableRocket(
                        null,
                        fromDate,
                        toDate),
                "The output stream cannot be null");
    }

    @Test
    void testMostReliableRocketThrowsExceptionWhenFromDateNull() throws IOException {
        Path tempFile = Files.createTempFile("tempFile", ".txt");
        LocalDate toDate = LocalDate.parse("Mon Aug 10, 2020", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));

        try {
            try (FileOutputStream fileOutputStream = new FileOutputStream(tempFile.toFile())) {
                assertThrows(IllegalArgumentException.class, () -> mjtSpaceScanner.saveMostReliableRocket(
                                fileOutputStream,
                                null,
                                toDate),
                        "From date cannot be null");
            }
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    void testMostReliableRocketThrowsExceptionWhenToDateNull() throws IOException {

        Path tempFile = Files.createTempFile("tempFile", ".txt");
        LocalDate fromDate = LocalDate.parse("Wed Oct 09, 2019", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));

        try {
            try (FileOutputStream fileOutputStream = new FileOutputStream(tempFile.toFile())) {
                assertThrows(IllegalArgumentException.class, () -> mjtSpaceScanner.saveMostReliableRocket(
                                fileOutputStream,
                                fromDate,
                                null),
                        "To date cannot be null");
            }
        } finally {
            Files.deleteIfExists(tempFile);
        }

    }

    @Test
    void testMostReliableRocketThrowsExceptionWhenToBeforeFrom() throws IOException {
        Path tempFile = Files.createTempFile("tempFile", ".txt");
        LocalDate toDate = LocalDate.parse("Wed Oct 09, 2019", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));
        LocalDate fromDate = LocalDate.parse("Mon Aug 10, 2020", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));

        try {
            try (FileOutputStream fileOutputStream = new FileOutputStream(tempFile.toFile())) {
                assertThrows(TimeFrameMismatchException.class, () -> mjtSpaceScanner.saveMostReliableRocket(
                                fileOutputStream,
                                fromDate,
                                toDate),
                        "To date cannot be null");
            }
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    void testMostReliableRocketExistsWhenNoMissionsReturns() throws IOException {
        // AI, used AI here to check was the output called for writting
        OutputStream mockOs = mock(OutputStream.class);
        LocalDate fromDate = LocalDate.parse("Wed Oct 09, 2019", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));
        LocalDate toDate = LocalDate.parse("Mon Aug 10, 2020", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));
        MJTSpaceScanner tempMJTSpaceScanner = new MJTSpaceScanner(
                new StringReader(""),
                new StringReader(""),
                null);

        tempMJTSpaceScanner.saveMostReliableRocket(mockOs, fromDate, toDate);

        verify(mockOs, never()).write(any());
    }

    @Test
    void testMostReliableRocketExistsWhenNoRocketsReturns() throws IOException {
        // AI, used AI here to check was the output called for writting
        OutputStream mockOs = mock(OutputStream.class);
        LocalDate fromDate = LocalDate.parse("Wed Oct 09, 2019", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));
        LocalDate toDate = LocalDate.parse("Mon Aug 10, 2020", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));
        MJTSpaceScanner tempMJTSpaceScanner = new MJTSpaceScanner(
                new StringReader(""),
                new StringReader("149,\"Delta IV Medium+ (5,2)\",https://en.wikipedia.org/wiki/Delta_IV,66.4 m\n" +
                        "148,\"Delta IV Medium+ (4,2)\",https://en.wikipedia.org/wiki/Delta_IV,62.5 m"),
                null);

        tempMJTSpaceScanner.saveMostReliableRocket(mockOs, fromDate, toDate);

        verify(mockOs, never()).write(any());
    }

    @Test
    void testGetMostReliableRocketThrowsCipherExceptionWithMessageAndThrowable() throws IOException {
        // AI, used AI here to check was the output called for writting
        try (ByteArrayOutputStream bufferedOutputStream = new ByteArrayOutputStream()) {
            LocalDate fromDate =
                    LocalDate.parse("Wed Oct 09, 2019", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));
            LocalDate toDate =
                    LocalDate.parse("Mon Aug 10, 2020", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));

            MJTSpaceScanner tempMJTSpaceScanner = new MJTSpaceScanner(
                    new StringReader(missionsDataSet),
                    new StringReader(rocketDataSet),
                    null);

            assertThrows(CipherException.class,
                    () -> tempMJTSpaceScanner.saveMostReliableRocket(bufferedOutputStream, fromDate, toDate));
        }
    }

    @Test
    void testGetMostReliableRocketThrowsCipherExceptionWithMessage() throws IOException {
        // AI, used AI here to check was the output called for writting
        try (ByteArrayOutputStream bufferedOutputStream = new ByteArrayOutputStream()) {
            LocalDate fromDate =
                    LocalDate.parse("Wed Oct 09, 2019", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));
            LocalDate toDate =
                    LocalDate.parse("Mon Aug 10, 2020", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));

            MJTSpaceScanner tempMJTSpaceScanner = mock(MJTSpaceScanner.class);
            doThrow(new CipherException("Errors when encrypting"))
                    .when(tempMJTSpaceScanner).saveMostReliableRocket(bufferedOutputStream, fromDate, toDate);

            assertThrows(CipherException.class,
                    () -> tempMJTSpaceScanner.saveMostReliableRocket(bufferedOutputStream, fromDate, toDate));
        }
    }

    @Test
    void testGetMostVariableRocketSuccess()
            throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        String mostReliableRocket = getMostReliableRocketFromDataSet();

        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        // encrypting the most reliable rocket
        byte[] encryptedRocket;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             CipherOutputStream cos = new CipherOutputStream(baos, cipher)) {
            cos.write(mostReliableRocket.getBytes(StandardCharsets.UTF_8));
            cos.close();
            encryptedRocket = baos.toByteArray();
        }

        LocalDate fromDate = LocalDate.parse("Wed Oct 09, 2019", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));
        LocalDate toDate = LocalDate.parse("Mon Aug 10, 2020", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));

        ByteArrayOutputStream toEncrypt = new ByteArrayOutputStream();
        mjtSpaceScanner.saveMostReliableRocket(toEncrypt, fromDate, toDate);
        byte[] encryptedRocketSpaceScanner = toEncrypt.toByteArray();

        assertEquals(encryptedRocket.length, encryptedRocketSpaceScanner.length,
                "There is a mismatch between the sizes of the encrypted rocket");
        for (int i = 0; i < encryptedRocketSpaceScanner.length; i++) {
            assertEquals(encryptedRocket[i], encryptedRocketSpaceScanner[i],
                    "There is a missmatch between the bytes from the encryption");
        }
    }

    @Test
    void testGetMostVariableRocketSuccess_RocketLaunchFallsOnToDate()
            throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        String mostReliableRocket = getMostReliableRocketFromDataSet();

        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        // encrypting the most reliable rocket
        byte[] encryptedRocket;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             CipherOutputStream cos = new CipherOutputStream(baos, cipher)) {
            cos.write(mostReliableRocket.getBytes(StandardCharsets.UTF_8));
            cos.close();
            encryptedRocket = baos.toByteArray();
        }

        LocalDate fromDate = LocalDate.parse("Wed Oct 09, 2019", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));
        LocalDate toDate = LocalDate.parse("Fri Aug 07, 2020", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));

        ByteArrayOutputStream toEncrypt = new ByteArrayOutputStream();
        mjtSpaceScanner.saveMostReliableRocket(toEncrypt, fromDate, toDate);
        byte[] encryptedRocketSpaceScanner = toEncrypt.toByteArray();

        assertEquals(encryptedRocket.length, encryptedRocketSpaceScanner.length,
                "There is a mismatch between the sizes of the encrypted rocket");
        for (int i = 0; i < encryptedRocketSpaceScanner.length; i++) {
            assertEquals(encryptedRocket[i], encryptedRocketSpaceScanner[i],
                    "There is a missmatch between the bytes from the encryption");
        }
    }

    @Test
    void testGetMostVariableRocketSuccess_IncreasedTimePeriod()
            throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        String mostReliableRocket = getMostReliableRocketFromDataSet();

        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        // encrypting the most reliable rocket
        byte[] encryptedRocket;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             CipherOutputStream cos = new CipherOutputStream(baos, cipher)) {
            cos.write(mostReliableRocket.getBytes(StandardCharsets.UTF_8));
            cos.close();
            encryptedRocket = baos.toByteArray();
        }

        LocalDate fromDate = LocalDate.parse("Thu Apr 04, 2019", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));
        LocalDate toDate = LocalDate.parse("Fri Aug 07, 2020", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));

        ByteArrayOutputStream toEncrypt = new ByteArrayOutputStream();
        mjtSpaceScanner.saveMostReliableRocket(toEncrypt, fromDate, toDate);
        byte[] encryptedRocketSpaceScanner = toEncrypt.toByteArray();

        assertEquals(encryptedRocket.length, encryptedRocketSpaceScanner.length,
                "There is a mismatch between the sizes of the encrypted rocket");
        for (int i = 0; i < encryptedRocketSpaceScanner.length; i++) {
            assertEquals(encryptedRocket[i], encryptedRocketSpaceScanner[i],
                    "There is a missmatch between the bytes from the encryption");
        }
    }

    @Test
    void testGetMostVariableRocketNoRocketsInTimePeriodShouldReturn() throws IOException {
        // AI, used AI here to check was the output called for writting
        OutputStream mockOs = mock(OutputStream.class);
        LocalDate fromDate = LocalDate.parse("Mon Dec 08, 2025", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));
        LocalDate toDate = LocalDate.parse("Mon Dec 22, 2025", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));
        MJTSpaceScanner tempMJTSpaceScanner = new MJTSpaceScanner(
                new StringReader(missionsDataSet),
                new StringReader(rocketDataSet),
                null);

        tempMJTSpaceScanner.saveMostReliableRocket(mockOs, fromDate, toDate);

        verify(mockOs, never()).write(any());
    }

    private String getMostReliableRocketFromDataSet() {
        BufferedReader readerMission = new BufferedReader(new StringReader(missionsDataSet));
        BufferedReader readerRocket = new BufferedReader(new StringReader(rocketDataSet));
        List<Mission> missionList = readerMission.lines().skip(1).map(Mission::of).toList();
        List<Rocket> rocketList = readerRocket.lines().skip(1).map(Rocket::of).toList();

        LocalDate fromDate = LocalDate.parse("Wed Oct 09, 2019", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));
        LocalDate toDate = LocalDate.parse("Mon Aug 10, 2020", DateTimeFormatter.ofPattern("EEE MMM d, yyyy"));

        // predicate that checks is a mission in the time period
        Predicate<Mission> isMissionInPeriod = mission ->
                ((mission.date().equals(fromDate) || mission.date().isAfter(fromDate)) &&
                        (mission.date().isBefore(toDate) || (mission.date().equals(toDate))));

        // gets all the missions in the time period and rockets used in this time period
        List<Mission> allMissionsInTimeFrame = missionList.stream().filter(isMissionInPeriod).toList();
        Set<String> allRocketNames = allMissionsInTimeFrame.stream()
                .map(mission -> mission.detail().rocketName()).collect(Collectors.toSet());

        Map<String, Long> allSuccessfulMissionsPerRocket = allMissionsInTimeFrame.stream()
                .filter(mission -> mission.missionStatus() == MissionStatus.SUCCESS)
                .collect(Collectors.groupingBy(
                        mission -> mission.detail().rocketName(),
                        Collectors.counting()
                ));

        Map<String, Long> allFailureMissionsPerRocket = allMissionsInTimeFrame.stream()
                .filter(mission -> mission.missionStatus() == MissionStatus.FAILURE
                        || mission.missionStatus() == MissionStatus.PRELAUNCH_FAILURE
                        || mission.missionStatus() == MissionStatus.PARTIAL_FAILURE)
                .collect(Collectors.groupingBy(
                        mission -> mission.detail().rocketName(),
                        Collectors.counting()
                ));

        Function<String, Double> reliabilityCalc = rocketName ->
                ((2.0d * allSuccessfulMissionsPerRocket.getOrDefault(rocketName, (long) 0)) +
                        allFailureMissionsPerRocket.getOrDefault(rocketName, (long) 0))
                        / (2.0d * (allFailureMissionsPerRocket.getOrDefault(rocketName, (long) 0) +
                        allSuccessfulMissionsPerRocket.getOrDefault(rocketName, (long) 0)));

        String mostReliableRocket = allRocketNames.stream()
                .collect(Collectors.toMap(
                        rocketName -> rocketName,
                        reliabilityCalc))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .get().getKey();

        return mostReliableRocket;
    }


}
