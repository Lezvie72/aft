package utils.helpers

import models.user.classes.*
import utils.Environment
import utils.helpers.Users.Stand.*

class Users {

    companion object {
        val USER = when (Environment.stand) {
            DEVELOP -> DefaultUser("aft.uat.sdex+0_keys@gmail.com")
            RELEASE -> DefaultUser("aft.uat.sdex+0_keys@gmail.com")
            PREPROD -> DefaultUser("aft.uat.sdex+0_keys@gmail.com")
            PROD -> DefaultUser("aft.uat.sdex+0_keys@gmail.com")
            SHARED -> DefaultUser("aft.uat.sdex+0_keys@gmail.com")
            TOKEN_TRUST -> DefaultUser()
        }

        val ATM_USER_FOR_RECOVERY = when (Environment.stand) {
            DEVELOP -> DefaultUser()
            RELEASE -> DefaultUser("aft.uat.sdex+recovery1@gmail.com")
            PREPROD -> DefaultUser("aft.uat.sdex+recovery1@gmail.com")
            PROD -> DefaultUser()
            SHARED -> DefaultUser("aft.uat.sdex+recovery1@gmail.com")
            TOKEN_TRUST -> DefaultUser("aft.uat.sdex+recovery@gmail.com")
        }

        val ATM_USER_FOR_CHANGE_PASSWORD = when (Environment.stand) {
            DEVELOP -> DefaultUser()
            RELEASE -> DefaultUser("aft.uat.sdex+change_password@gmail.com")
            PREPROD -> DefaultUser("aft.uat.sdex+change_password@gmail.com")
            PROD -> DefaultUser("aft.uat.sdex+change_password@gmail.com")
            SHARED -> DefaultUser("aft.uat.sdex+change_password@gmail.com")
            TOKEN_TRUST -> DefaultUser("aft.uat.sdex+change_password@gmail.com")
        }

        val ATM_USER_WITH_BLOCK_WALLET = when (Environment.stand) {
            DEVELOP -> UserWithMainWallet2FA(
                mainWallet = MainWallet(),
                oAuthSecret = ""
            )
            RELEASE -> UserWithMainWallet2FA(
                "aft.uat.sdex+block_wallet@gmail.com",//todo заблокировать кошелек
                "ZPLASG7TKJFHCNAT",
                MainWallet(
                    publicKey = "6a73008a377ba4e9987a8b3399a0ba193be39a3cf3be27f781c2ed8ce1b76ba7",
                    secretKey = "edb7ddc4cf311b1766fa818c6a6671dedbaf947eae5f11af9a4d28abe289c6f26a73008a377ba4e9987a8b3399a0ba193be39a3cf3be27f781c2ed8ce1b76ba7"
                )
            )
            PREPROD -> UserWithMainWallet2FA(
                email = "aft.uat.sdex+block_wallet@gmail.com",
                mainWallet = MainWallet(),
                oAuthSecret = ""
            )//todo завести пользака и заблокировать кошелек
            PROD -> UserWithMainWallet2FA(
                email = "aft.uat.sdex+block_wallet@gmail.com",
                mainWallet = MainWallet(),
                oAuthSecret = ""
            )//todo завести пользака и заблокировать кошелек
            SHARED -> UserWithMainWallet2FA(
                email = "aft.uat.sdex+block_wallet@gmail.com",
                mainWallet = MainWallet(),
                oAuthSecret = ""
            )//todo завести пользака и заблокировать кошелек
            TOKEN_TRUST -> UserWithMainWallet2FA(
                email = "aft.uat.sdex+block_wallet@gmail.com",
                mainWallet = MainWallet(),
                oAuthSecret = ""
            )
        }

        // Atm user: OAuth, register_wallet_autottest (registerWalletAutotest)
//ATM_USER_FOR_REGISTER_WALLET
        val ATM_USER_FOR_REGISTER_WALLET = when (Environment.stand) {
            DEVELOP -> DefaultUserWith2FA(oAuthSecret = "")
            RELEASE -> DefaultUserWith2FA(
                //registerWalletAutotest2
                "aft.uat.sdex+for_register_wallet_2@gmail.com",
                "PBUHLPK6TEO2CYMF"
            )
            PREPROD -> DefaultUserWith2FA(
                "aft.uat.sdex+for_register_wallet@gmail.com",
                "5CPYQGAEB73G64XH"
            )
            PROD -> DefaultUserWith2FA(
                "aft.uat.sdex+for_register_wallet@gmail.com",
                ""
            )//todo завести пользака
            SHARED -> DefaultUserWith2FA(
                "aft.uat.sdex+for_register_wallet_1@gmail.com",
                "VKR3FI4GZOCQL2G4"
            )
            TOKEN_TRUST -> DefaultUserWith2FA(
                "aft.uat.sdex+for_register_wallet_1@gmail.com",
                "Q74MDMRIQI5Z667X"
            )
        }

        // atm_user_for_employees
        val ATM_USER_FOR_EMPLOYEES = when (Environment.stand) {
            DEVELOP -> DefaultUser()
            RELEASE -> DefaultUser("aft.uat.sdex+for_employees_1@gmail.com")
            PREPROD -> DefaultUser("aft.uat.sdex+for_employees@gmail.com")
            PROD -> DefaultUser()
            SHARED -> DefaultUser("aft.uat.sdex+for_employees@gmail.com")
            TOKEN_TRUST -> DefaultUser("aft.uat.sdex+for_employees@gmail.com")
        }

        // atm_user_for_employees_kyc1 (netrogatEmpAutotest)
        val ATM_USER_FOR_EMPLOYEES_KYC1 = when (Environment.stand) {
            DEVELOP -> DefaultUser()
            RELEASE -> DefaultUser("aft.uat.sdex+for_employees_kyc1@gmail.com")
            PREPROD -> DefaultUser("aft.uat.sdex+for_employees_kyc1@gmail.com")
            PROD -> DefaultUser()
            SHARED -> DefaultUser("aft.uat.sdex+for_employees_kyc1@gmail.com")
            TOKEN_TRUST -> DefaultUser("aft.uat.sdex+for_employees_kyc1@gmail.com")
        }

        // USER_FOR_BANK_ACC, autotestBankAcc
        val ATM_USER_FOR_BANK_ACC = when (Environment.stand) {
            DEVELOP -> DefaultUser()
            RELEASE -> DefaultUser("aft.uat.sdex+for_bank_acc@gmail.com")
            PREPROD -> DefaultUser("aft.uat.sdex+for_bank_acc@gmail.com")
            PROD -> DefaultUser()
            SHARED -> DefaultUser("aft.uat.sdex+for_bank_acc@gmail.com")
            TOKEN_TRUST -> DefaultUser()
        }

        // USER_FOR_BANK_ACC_ONE,autotestBankAccOne
        val ATM_USER_FOR_BANK_ACC_ONE = when (Environment.stand) {
            DEVELOP -> DefaultUser()
            RELEASE -> DefaultUser("aft.uat.sdex+for_bank_acc_one@gmail.com")
            PREPROD -> DefaultUser("aft.uat.sdex+for_bank_acc_one@gmail.com")
            PROD -> DefaultUser()
            SHARED -> DefaultUser("aft.uat.sdex+for_bank_acc_one@gmail.com")
            TOKEN_TRUST -> DefaultUser()
        }

        val ATM_USER_FOR_BANK_ACC_TWO = when (Environment.stand) {
            DEVELOP -> DefaultUser()
            RELEASE -> DefaultUser("aft.uat.sdex+for_bank_acc_two@gmail.com")
            PREPROD -> DefaultUser("aft.uat.sdex+for_bank_acc_two@gmail.com")
            PROD -> DefaultUser()
            SHARED -> DefaultUser("aft.uat.sdex+for_bank_acc_two@gmail.com")
            TOKEN_TRUST -> DefaultUser()
        }

        // atm_user_kyc0
        val ATM_USER_KYC0 = when (Environment.stand) {
            DEVELOP -> DefaultUser()
            RELEASE -> DefaultUser("aft.uat.sdex+0_aftuser_1@gmail.com")
            PREPROD -> DefaultUser("aft.uat.sdex+0_aft_user@gmail.com")
            PROD -> DefaultUser()
            SHARED -> DefaultUser("aft.uat.sdex+0_aftuser@gmail.com")
            TOKEN_TRUST -> DefaultUser("aft.uat.sdex+0_aftuser@gmail.com")
        }

        // Atm user: KYC0, no 2FA, Autotest
        val ATM_USER_KYC0_2FA_NONE = when (Environment.stand) {
            DEVELOP -> DefaultUser()
            RELEASE -> DefaultUser("aft.uat.sdex+atm_2fa_none_4@gmail.com")
            PREPROD -> DefaultUser("aft.uat.sdex+atm_ver0_2fa_none_1@gmail.com")
            PROD -> DefaultUser()
            SHARED -> DefaultUser("aft.uat.sdex+atm_ver0_2fa_none_1@gmail.com")
            TOKEN_TRUST -> DefaultUser("aft.uat.sdex+atm_ver0_2fa_none_1@gmail.com")
        }

        val ATM_USER_ADD_2FA_APP = when (Environment.stand) {
            DEVELOP -> DefaultUser()
            RELEASE -> DefaultUser("aft.uat.sdex+atm_add_2fa@gmail.com")
            PREPROD -> DefaultUser("aft.uat.sdex+atm_add_2fa2@gmail.com")
            PROD -> DefaultUser()
            SHARED -> DefaultUser("aft.uat.sdex+atm_add_2fa@gmail.com")
            TOKEN_TRUST -> DefaultUser("aft.uat.sdex+atm_add_2fa@gmail.com")
        }

        val ATM_USER_2FA_OAUTH = when (Environment.stand) {
            DEVELOP -> DefaultUserWith2FA(oAuthSecret = "")
            RELEASE -> DefaultUserWith2FA(
                "aft.uat.sdex+atm_oauth_1@gmail.com",
                "H7XE3AZGPNQIF4C6"
            )
            PREPROD -> DefaultUserWith2FA(
                "aft.uat.sdex+atm_ver1@gmail.com",
                "HSW2MYXD6KY7J4O6"
            )
            PROD -> DefaultUserWith2FA(oAuthSecret = "")
            SHARED -> DefaultUserWith2FA(
                "aft.uat.sdex+atm_ver1@gmail.com",
                "MZSCTCYD2ROEFSRC"
            )
            TOKEN_TRUST -> DefaultUserWith2FA(
                "aft.uat.sdex+atm_ver1@gmail.com",
                oAuthSecret = "B5LHZPYVWDNDDC37"
            )
        }

        // Atm user: OAuth, KYC, NoWallet (AutotestNoWallet)
        val ATM_USER_2FA_OAUTH_NOWALLETS = when (Environment.stand) {
            DEVELOP -> DefaultUserWith2FA(oAuthSecret = "")
            RELEASE -> DefaultUserWith2FA(
                "aft.uat.sdex+atm_oauth_nowallets_1@gmail.com",
                "PRRZZI4WN4H3BGG5"
            )
            PREPROD -> DefaultUserWith2FA(
                "aft.uat.sdex+atm_oauth_nowallets_1@gmail.com",
                "VW346WZGXJVXIDMB"
            )
            PROD -> DefaultUserWith2FA(oAuthSecret = "")
            SHARED -> DefaultUserWith2FA(
                "aft.uat.sdex+atm_oauth_nowallets_1@gmail.com",
                "PN5DYC3HY3AUGIZS"
            )
            TOKEN_TRUST -> DefaultUserWith2FA(
                "aft.uat.sdex+atm_oauth_nowallets_1@gmail.com",
                "PQLGTKVSC7QMWZ5V"
            )
        }

        // Atm user: OAauth, KYC, reserved for 2FA Block check
        val ATM_USER_2FA_OAUTH_BLOCK = when (Environment.stand) {
            DEVELOP -> DefaultUserWith2FA(oAuthSecret = "")
            RELEASE -> DefaultUserWith2FA(
                "aft.uat.sdex+atm_ver01_blocked@gmail.com",
                oAuthSecret = "AZFS3OSLCIM7WTSN"
            )
            PREPROD -> DefaultUserWith2FA(
                "aft.uat.sdex+atm_ver1_blocked@gmail.com",
                oAuthSecret = "7HBANXI3DESUFNJ6"
            )
            PROD -> DefaultUserWith2FA(oAuthSecret = "")
            SHARED -> DefaultUserWith2FA(
                "aft.uat.sdex+atm_ver1_blocked@gmail.com",
                oAuthSecret = "7HBANXI3DESUFNJ6"
            )
            TOKEN_TRUST -> DefaultUserWith2FA(
                "aft.uat.sdex+atm_ver1_blocked@gmail.com",
                oAuthSecret = "VHCZJORLXN3FFANS"
            )
        }

        // Atm user: OAuth, Manual signature, autotestMain
        val ATM_USER_2FA_MANUAL_SIG_MAIN_WALLET = when (Environment.stand) {
            DEVELOP -> UserWithMainWallet2FA(
                "aft.uat.sdex+atm_oauth_main_1@gmail.com",
                "NS3VLQMHH4BYCOJM",
                MainWallet(
                    name = "Main 1",
                    publicKey = "b8cce0fe925edfdb715664f4791437107f74e1d580e11cfebc7eb925fa4c8b71",
                    secretKey = "3e79dfe2016cd08036c172ed8d2a254042a1c02121fcec182ee60bd57e44e87eb8cce0fe925edfdb715664f4791437107f74e1d580e11cfebc7eb925fa4c8b71"

                )
            )
            RELEASE -> UserWithMainWallet2FA(
                "aft.uat.sdex+atm_oauth_main_1@gmail.com",
                "44UI3OTLBQN3GSPZ",
                MainWallet(
                    name = "Main 2",
                    publicKey = "cce8f0eba39a28d3e94a54d89991c8afb19d2623ca4b1cd4c3822d0ab234d0c5",
                    secretKey = "5b7236b268bbbd57ce4cbe7be46ffe2d5373850c173bf5194873cea5c3553a79cce8f0eba39a28d3e94a54d89991c8afb19d2623ca4b1cd4c3822d0ab234d0c5"
                )
            )
            PREPROD -> UserWithMainWallet2FA(
                "aft.uat.sdex+atm_oauth_main_1@gmail.com",
                "IN4TEY6GPJKKPMHY",
                MainWallet(
                    name = "Main 1",
                    secretKey = "1a3baff3e770874d4f8d411354b190f97e37e7489a7243e941267c9a16f85022a6c3c6575617656bafacaf1f67166a486507aec895b3f20e8ec8dfc05a25d054",
                    publicKey = "a6c3c6575617656bafacaf1f67166a486507aec895b3f20e8ec8dfc05a25d054"
                )
            )
            PROD -> UserWithMainWallet2FA(
                mainWallet = MainWallet(),
                oAuthSecret = ""
            )
            SHARED -> UserWithMainWallet2FA(
                "aft.uat.sdex+atm_oauth_main_1@gmail.com",
                "R3MGG33UEMTH2UJ6",
                MainWallet(
                    secretKey = "1a3baff3e770874d4f8d411354b190f97e37e7489a7243e941267c9a16f85022a6c3c6575617656bafacaf1f67166a486507aec895b3f20e8ec8dfc05a25d054",
                    publicKey = "a6c3c6575617656bafacaf1f67166a486507aec895b3f20e8ec8dfc05a25d054"
                )
            )
            TOKEN_TRUST -> UserWithMainWallet2FA(
                "aft.uat.sdex+atm_oauth_main_1@gmail.com",
                "7K7FPVUD2BB2HOV3",
                MainWallet(
                    name = "Main 3",
                    secretKey = "fbd4672aecf2f1097e590475658926270714b1f3de10353a0876a914e5bdac0f93a346c5c60d357da7b2b11c6f27b1b9e658cd86901cef88960ae14f59011d67",
                    publicKey = "93a346c5c60d357da7b2b11c6f27b1b9e658cd86901cef88960ae14f59011d67"
                )
            )
        }

        // TODO: Неправильный ключ в паблике
// Atm user: OAuth, Manual signature, OTF1 (autotestOTF)
        val ATM_USER_2FA_MANUAL_SIG_OTF_WALLET = when (Environment.stand) {
            DEVELOP -> UserWithMainWalletAndOtf2FA(
                "aft.uat.sdex+atm_otf_1@gmail.com",
                "VODKJVFPASFAXMGA",
                otfWallet = OtfWallet(
                    publicKey = "834cf8bef87aa0c97c0422927efa30b986744770bf733ad897de3c6134fee243",
                    secretKey = "fed4e0fe14063310755f3658339db1293fd4bb74eaf718f894e2734293d56871834cf8bef87aa0c97c0422927efa30b986744770bf733ad897de3c6134fee243"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "35cee0013195030ac44e2f6b6f1a9839e94c01da19236ce5b55992353b9f7364",
                    secretKey = "d8b2db15ed8e10aaf5640b8cf65e4386c32dd460e034ccc3caf92580b11690a335cee0013195030ac44e2f6b6f1a9839e94c01da19236ce5b55992353b9f7364"
                )
            )
            RELEASE -> UserWithMainWalletAndOtf2FA(
                "aft.uat.sdex+atm_otf_1@gmail.com",
                "BKR7Z5HTFP3ZDFW7",
                otfWallet = OtfWallet(
                    publicKey = "c6f4e683bb4992acf09c9cba9e266c696aff77fca923db2a472b813e37f9e96f",
                    secretKey = "f543ca6ff42b8266e491236fe84cedb1eb3f591a74f9ad48e99fc296f30d5783c6f4e683bb4992acf09c9cba9e266c696aff77fca923db2a472b813e37f9e96f"
                ),
                mainWallet = MainWallet(
                    name = "Main 3",
                    publicKey = "ce3c72a5fe5ede7b2b3edeab7f40a845a8e51fc14b66042e2e5298e64f6ac68e",
                    secretKey = "bd7b851eb16d0448fa79ee1969467bcdf63cc2f127e203122aaaed043342a4b4ce3c72a5fe5ede7b2b3edeab7f40a845a8e51fc14b66042e2e5298e64f6ac68e"
                )
            )
            PREPROD -> UserWithMainWalletAndOtf2FA(
                "aft.uat.sdex+atm_otf_1@gmail.com",
                "TRXXA4OQ4ZFHY7AM",
                otfWallet = OtfWallet(
                    publicKey = "164975b37a91b984719b5cdde0f0831a853ff454ba66315493b527c0c781a147",
                    secretKey = "ff90af176f7fd254ffa4bfef29583efe44de1fcbb0049704b045274effe45cfb164975b37a91b984719b5cdde0f0831a853ff454ba66315493b527c0c781a147"
                ),
                mainWallet = MainWallet(
                    name = "Main 2",
                    publicKey = "674331cdd443ecd1dc4111315bfec6873c615e38224a6e8e1468bf0aa856504c",
                    secretKey = "c502f92a112223d376b70a7e36e7c1d75cd85f5bb9cd096851948dfd031c2544674331cdd443ecd1dc4111315bfec6873c615e38224a6e8e1468bf0aa856504c"
                )
            )
            PROD -> UserWithMainWalletAndOtf2FA(
                oAuthSecret = "",
                otfWallet = OtfWallet(),
                mainWallet = MainWallet()
            )
            SHARED -> UserWithMainWalletAndOtf2FA(
                "aft.uat.sdex+atm_otf_1@gmail.com",
                "LY2WAFTFFFVGJZY5",
                otfWallet = OtfWallet(
                    publicKey = "164975b37a91b984719b5cdde0f0831a853ff454ba66315493b527c0c781a147",
                    secretKey = "ff90af176f7fd254ffa4bfef29583efe44de1fcbb0049704b045274effe45cfb164975b37a91b984719b5cdde0f0831a853ff454ba66315493b527c0c781a147"
                ),
                mainWallet = MainWallet(
                    name = "Main 2",
                    publicKey = "f59ebc6b279450c264837c0bdabc04a0f58c20b0237b24b3a536b28ebf72a1e4",
                    secretKey = "e4bc08a87b5d52d47b6a36ef8b0b6e2726849da1398ed5272a1e35982518abf1f59ebc6b279450c264837c0bdabc04a0f58c20b0237b24b3a536b28ebf72a1e4"
                )
            )
            TOKEN_TRUST -> UserWithMainWalletAndOtf2FA(
                "aft.uat.sdex+atm_otf_1@gmail.com",
                "W3ZLMZOS7NARY3PR",
                otfWallet = OtfWallet(
                    publicKey = "6c9409244aed6291df3d58de280a2cd38e43df7f2efc3d41a1e6cf279e3a544c",
                    secretKey = "6d15de561c8809d182c09be61fc73a435b5d5512758e748970195f5f9cb208646c9409244aed6291df3d58de280a2cd38e43df7f2efc3d41a1e6cf279e3a544c"
                ),
                mainWallet = MainWallet(
                    name = "Main 2",
                    publicKey = "1b2d1d57717b7838c30078cf24bcb2e2a310abd3bce9a57beb890e4d6bcd5d82",
                    secretKey = "58a7885bc8e89d8d057641c6d929999a5b6d3f8abcd0b19474ed3f744d340d701b2d1d57717b7838c30078cf24bcb2e2a310abd3bce9a57beb890e4d6bcd5d82"
                )
            )
        }

        // Atm user: WITHOUT OAuth and OTF, OTFWITHOUTOAUTH_AUTOTEST
        val ATM_USER_WITHOUT2FA_MANUAL_SIG_OTF_WALLET = when (Environment.stand) {
            DEVELOP -> UserWithMainWalletAndOtf(
                "aft.uat.sdex+atm_otf_withoutoauth@gmail.com",
                otfWallet = OtfWallet(
                    publicKey = "a0150023a2fdb64583dc21fbe2b5408bac05c04f6262aaccfbfcc8f2d4a8f149",
                    secretKey = "d5cafadd93f29e34490c08b9448bc023c6578ad9cc50d8e4fc41022d2da982a7a0150023a2fdb64583dc21fbe2b5408bac05c04f6262aaccfbfcc8f2d4a8f149"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "66c4813cdcac6717b52c668ba46cd08eb86e5a4b0e0aac71a57e821740db628b",
                    secretKey = "d4195b243af1f78ef326baabe56f3bd5c533cb65598c65f33ec0cd5b67671bac66c4813cdcac6717b52c668ba46cd08eb86e5a4b0e0aac71a57e821740db628b"
                )
            )
            RELEASE -> UserWithMainWalletAndOtf(
                "aft.uat.sdex+atm_otf_withoutoauth@gmail.com",
                otfWallet = OtfWallet(
                    publicKey = "ee92238a28f5bb1f54e9ebc61392f5284581f578f20ab9611ec1c78f5b748f0a",
                    secretKey = "850d34e703050f177e306a83fe9a563c9531db7d717682888f3a6d4ec2d3fd9eee92238a28f5bb1f54e9ebc61392f5284581f578f20ab9611ec1c78f5b748f0a"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "fb3df6e221151f2f24b72b7b730fcfbea2623f6e9e2e245b00ef6cdb9c3c062f",
                    secretKey = "57f8f13b7271a0278bbfd62ec0727ee3571d654ac5f0de4601a520f19459b30afb3df6e221151f2f24b72b7b730fcfbea2623f6e9e2e245b00ef6cdb9c3c062f"
                )
            )
            PREPROD -> UserWithMainWalletAndOtf(
                "aft.uat.sdex+atm_otf_withoutoauth@gmail.com",
                otfWallet = OtfWallet(
                    publicKey = "d0750fb742256794d6b1edca2896b2e8f3c86640a8323585362015d56713efbc",
                    secretKey = "fa72ef9663031a3a7ee10fa74ab314c1c1ea2fc131c9f5e3ecbd68f6b16f237ed0750fb742256794d6b1edca2896b2e8f3c86640a8323585362015d56713efbc"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "3fd9e9e64d18e07448e55a555bf48e5e71374fddcf38f0a592b5a3ca02500b95",
                    secretKey = "71fe528a7a0f84258aa22371776e26bdd05fd7094fa9b9d9efc0410eef39aebd3fd9e9e64d18e07448e55a555bf48e5e71374fddcf38f0a592b5a3ca02500b95"
                )
            )
            PROD -> UserWithMainWalletAndOtf(
                otfWallet = OtfWallet(),
                mainWallet = MainWallet()
            )
            SHARED -> UserWithMainWalletAndOtf(
                "aft.uat.sdex+atm_otf_withoutoauth@gmail.com",
                otfWallet = OtfWallet(
                    publicKey = "d0750fb742256794d6b1edca2896b2e8f3c86640a8323585362015d56713efbc",
                    secretKey = "fa72ef9663031a3a7ee10fa74ab314c1c1ea2fc131c9f5e3ecbd68f6b16f237ed0750fb742256794d6b1edca2896b2e8f3c86640a8323585362015d56713efbc"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "5bb277e16cf96042b352fa445f186ae839172d9bb2567ca3f496391388e128c7",
                    secretKey = "ea0d8055a3b827bb4d01e53f21c904d33faf0ab78472bbba2b447562f013bb015bb277e16cf96042b352fa445f186ae839172d9bb2567ca3f496391388e128c7"

                )
            )
            TOKEN_TRUST -> UserWithMainWalletAndOtf(
                "aft.uat.sdex+atm_otf_withoutoauth@gmail.com",
                otfWallet = OtfWallet(
                    publicKey = "119385401500fc1a9c665f4436ce68110078b97475c9162b633a5f7008be4591",
                    secretKey = "d4c734bae1f65bc6d1a850db63db8cf1376c1d77c7f114862fded0fd76b8dc45119385401500fc1a9c665f4436ce68110078b97475c9162b633a5f7008be4591"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "2bf957beb0e1eea4452d6078d9ea78112a2b0981ce92ff1904ff98a98317659b",
                    secretKey = "f462d044ca247df4fc1c3071e313c58de1cf81d63df7cd24e333c4731a75fda42bf957beb0e1eea4452d6078d9ea78112a2b0981ce92ff1904ff98a98317659b"
                )
            )
        }

        // Atm user: OAuth, Manual signature, OTFWalletForOTFAUTOTEST
        val ATM_USER_2FA_MANUAL_SIG_OTF_WALLET_FOR_OTF = when (Environment.stand) {
            DEVELOP -> UserWithMainWalletAndOtf2FA(
                "aft.uat.sdex+atm_otf_for_otf@gmail.com",
                "F7AYUMEPPI2PDP32",
                otfWallet = OtfWallet(
                    name = "OTF 1",
                    publicKey = "0f0e2c6411a3efcd7bcfab7532f97a0d1ec5fdcb944dc719ba4610beaafe5998",
                    secretKey = "0152627e26c4f4d86099aa9bb9cd8748fc18e3a28780872dec2b1e5421d4b60b0f0e2c6411a3efcd7bcfab7532f97a0d1ec5fdcb944dc719ba4610beaafe5998"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "5de69a3225fd0d9e0dfc5c051849ed0e11eaac29d4785fecf019620069e137d4",
                    secretKey = "980e10f88071935ffd9e020a8d5bf2ea8164826173c34dfa8c633f99c2bdfc945de69a3225fd0d9e0dfc5c051849ed0e11eaac29d4785fecf019620069e137d4"
                )
            )
            RELEASE -> UserWithMainWalletAndOtf2FA(
                "aft.uat.sdex+atm_otf_for_otf@gmail.com",
                "UUY77VF2C2BDMTEV",
                otfWallet = OtfWallet(
                    name = "OTF 1",
                    publicKey = "68fbb752bd2a8dcc4e7ac972e6188e91059c382720f60499fd8bb6f1bb85fa91",
                    secretKey = "396de4db5e14347fd5cc96dc1773d51663acebca5b4193ad5809d8365b3c68dc68fbb752bd2a8dcc4e7ac972e6188e91059c382720f60499fd8bb6f1bb85fa91"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "021118ad5f95226733d20f80051be24915acedf119d75646d0ad48bb45447b83",
                    secretKey = "d23108768b558d361394ba52bc1a3229e946edf9c20cd3a6e612eab17a6bca83021118ad5f95226733d20f80051be24915acedf119d75646d0ad48bb45447b83"
                )
            )
            PREPROD -> UserWithMainWalletAndOtf2FA(
                "aft.uat.sdex+atm_otf_for_otf@gmail.com",
                "ENWZWKCHIODV54ZX",
                otfWallet = OtfWallet(
                    publicKey = "8170598d20d0c4ec54d8ac197aef794003881caff756a1778f8191d58ffeb8ac",
                    secretKey = "b13a3b6acae4420ca57ba6b9eb7b662a488b26e03519a8d123067fe0aef8aea48170598d20d0c4ec54d8ac197aef794003881caff756a1778f8191d58ffeb8ac"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "3b3bfb89aa3a8bffb1673a4c4d5b8f15a16fe5d88a2c430385ca0bc9d117c971",
                    secretKey = "324db353cc777afd4d5a478f5e9990a89bfaea4e167a8160c3bf002770c81ff03b3bfb89aa3a8bffb1673a4c4d5b8f15a16fe5d88a2c430385ca0bc9d117c971"

                )
            )
            PROD -> UserWithMainWalletAndOtf2FA(
                otfWallet = OtfWallet(),
                mainWallet = MainWallet(),
                oAuthSecret = ""
            )
            SHARED -> UserWithMainWalletAndOtf2FA(
                "aft.uat.sdex+atm_otf_for_otf@gmail.com",
                "BCFGGZZVVMPQOFMV",
                otfWallet = OtfWallet(
                    publicKey = "8170598d20d0c4ec54d8ac197aef794003881caff756a1778f8191d58ffeb8ac",
                    secretKey = "b13a3b6acae4420ca57ba6b9eb7b662a488b26e03519a8d123067fe0aef8aea48170598d20d0c4ec54d8ac197aef794003881caff756a1778f8191d58ffeb8ac"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "7f7348e28a255db4bbb356f2437e84a6b5fe6de5d11647b12ac23ef008837f2c",
                    secretKey = "ed6dcce1a91d735d02980539082d1a840d07ea6bb69016f1816c4bca0a87f6b27f7348e28a255db4bbb356f2437e84a6b5fe6de5d11647b12ac23ef008837f2c"
                )
            )
            TOKEN_TRUST -> UserWithMainWalletAndOtf2FA(
                "aft.uat.sdex+atm_otf_for_otf@gmail.com",
                "KJRHHP7ZWRDKY37A",
                otfWallet = OtfWallet(
                    publicKey = "94ed071506c59d7dcd4126d97ce67f7f23920ae4dc9ffd0c8bf12c2fc40732a3",
                    secretKey = "b1ac47e7dd02648d5f1a46fa2b74080b97133e2ed825fa70dda118e77bc1416f94ed071506c59d7dcd4126d97ce67f7f23920ae4dc9ffd0c8bf12c2fc40732a3"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "3db27831be766e3aa43eac0bc1c04ae36c7d00a44c2b841d88c8a7d5adcc8281",
                    secretKey = "f33aafc21c1a1d13bddbe8d8e8635f3d7c84b031daeab61c72a4213b79f56d293db27831be766e3aa43eac0bc1c04ae36c7d00a44c2b841d88c8a7d5adcc8281"
                )
            )
        }

        val ATM_USER_2FA_MANUAL_SIG_OTF2_WALLET = when (Environment.stand) {
            DEVELOP -> UserWithMultipleMainWallet2FA(
                walletList = listOf(),
                oAuthSecret = ""
            )
            RELEASE -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+atm_otf_2@gmail.com",
                "5DQ7VVUERP55ORJI",
                walletList = listOf(
                    MainWallet(
                        name = "Main",
                        publicKey = "e30737ac55aca77812d5b304299f1a6977d820a13d34adc86fd2e988b954a3d6",
                        secretKey = "a08768ad9f0dfd591d1fdeb4c44656e82f322913868e8fc01c390f4ef804896ee30737ac55aca77812d5b304299f1a6977d820a13d34adc86fd2e988b954a3d6"
                    ),
                    MainWallet(
                        name = "Main 2",
                        publicKey = "75ac4ff456097ee6c3c122e8e21ec46bb5cbbe6afa4c10d12dc2539a4fa540e3",
                        secretKey = "2be64ed6998774dbfccd7351adae0256177b91be24f89c3ec5a2093a3d99d3de75ac4ff456097ee6c3c122e8e21ec46bb5cbbe6afa4c10d12dc2539a4fa540e3"
                    )
                )
            )
            PREPROD -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+atm_otf2@gmail.com",
                "C7EYHDWPN3Q5GHBS",
                walletList = listOf(
                    MainWallet(
                        publicKey = "19bd97333666bf67d17aa71e8b9d3181e15d7a6d6e3114e050c7034561fe547a",
                        secretKey = "b85cf10891983ff47ad93c10a7abe3e6b84bcbca1d0b422c2087f80bbb0d5dc619bd97333666bf67d17aa71e8b9d3181e15d7a6d6e3114e050c7034561fe547a"
                    ),
                    MainWallet(
                        name = "Main 4",
                        publicKey = "2a7d2cbb9fa1a1b57b7898557a86185ccb987813fdb931307d4840a81465b42a",
                        secretKey = "b9fc349d63a5be6e0af01e4ff2ccfcf01f98f3b167066703993c9ffcc85d30cd2a7d2cbb9fa1a1b57b7898557a86185ccb987813fdb931307d4840a81465b42a"
                    )
                )
            )
            PROD -> UserWithMultipleMainWallet2FA(
                walletList = listOf(),
                oAuthSecret = ""
            )
            SHARED -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+atm_otf2@gmail.com",
                "ZTTHZKBWASGULB6Z",
                walletList = listOf(
                    MainWallet(
                        publicKey = "19bd97333666bf67d17aa71e8b9d3181e15d7a6d6e3114e050c7034561fe547a",
                        secretKey = "b85cf10891983ff47ad93c10a7abe3e6b84bcbca1d0b422c2087f80bbb0d5dc619bd97333666bf67d17aa71e8b9d3181e15d7a6d6e3114e050c7034561fe547a"
                    ),
                    MainWallet(
                        name = "Main 4",
                        publicKey = "370db4db1ffd08700e7667e8675f9262086c6831fc7435d2a460535bf92cd7fe",
                        secretKey = "ec1e8d31dafb2f0f6ad961cf6bf0eedf2e385bca7c58a8436bef176836fcb1a6370db4db1ffd08700e7667e8675f9262086c6831fc7435d2a460535bf92cd7fe"

                    )
                )
            )
            TOKEN_TRUST -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+atm_otf2@gmail.com",
                "OMT7XJ3XM2VFMMUV",
                walletList = listOf(
                    MainWallet(
                        name = "Main 1",
                        publicKey = "788248e42cc5f5056c1e9c0a683781529ec790b3609d8783027ac9cc5827cf4b",
                        secretKey = "80be24e014e5a3582f731a5b9e8fa070337f6b4e6032539cab1a9abf9ceecf45788248e42cc5f5056c1e9c0a683781529ec790b3609d8783027ac9cc5827cf4b"
                    ),
                    MainWallet(
                        name = "Main 4",
                        publicKey = "cad44687a47ed7d74c9b1bd652305ca4b7b243669970217cf2a196cfd2b27eb3",
                        secretKey = "7c829afa458cfcaf8fdb212fc44b9903754af35f073ed01a8765cf9b442a067dcad44687a47ed7d74c9b1bd652305ca4b7b243669970217cf2a196cfd2b27eb3"
                    )
                )
            )
        }

        // Atm user: 2Main wallets
        val ATM_USER_2MAIN_WALLET = when (Environment.stand) {
            DEVELOP -> UserWithMultipleMainWallet2FA(
                "",
                "",
                listOf()
            )
            RELEASE -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+atm_user_2mainwallet@gmail.com",
                "LIG3FRMOMSG5S7A3",
                listOf(
                    MainWallet(
                        name = "Main 1",
                        publicKey = "92086071d054f91bf6768a89ae34096b3ff45752d215727e4ba932cbe44f78ef",
                        secretKey = "33bcb3cea0c62c527e8d0db4b8775955b667cceb9ac9b2597fb48c7fd8e90ac092086071d054f91bf6768a89ae34096b3ff45752d215727e4ba932cbe44f78ef"

                    ),
                    MainWallet(
                        name = "Main",
                        publicKey = "be35e22c29ff6460b6a0d7cc0ff9c5510404b1a5e4fbe95e69a502845a48d036",
                        secretKey = "0a5170dee243c987d8414dfb5a0ed965cae2c52fe52da50a5d37c364e64439a9be35e22c29ff6460b6a0d7cc0ff9c5510404b1a5e4fbe95e69a502845a48d036"
                    )
                )
            )
            PREPROD -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+atm_user_2mainwallet@gmail.com",
                "TE5TQ3XCTPCFNFAT",
                listOf(
                    MainWallet(
                        name = "Main",
                        publicKey = "1ae94aaa5cc54dea7fd5f4d35a9184647dadb22530fd93710ed4331e01e23fd6",
                        secretKey = "4360f1f9a0317909d47895faee2901e01495785d2fd107353ce930a2df3a5dd31ae94aaa5cc54dea7fd5f4d35a9184647dadb22530fd93710ed4331e01e23fd6"
                    ),
                    MainWallet(
                        name = "Main 2",
                        publicKey = "36de9ce91a6e8eb03731187491c182c1d52cd53aeb3dac2b1f124a2cfb2c5045",
                        secretKey = "b11c46955fe9e0be4368595b443eb0134a954d6a8b7d434d8507c0b850fc3b3036de9ce91a6e8eb03731187491c182c1d52cd53aeb3dac2b1f124a2cfb2c5045"
                    )
                )
            )
            PROD -> UserWithMultipleMainWallet2FA(
                "",
                "",
                listOf()
            )
            SHARED -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+atm_user_2mainwallet@gmail.com",
                "K7UDT22VMJIPGQF4",
                listOf(
                    MainWallet(
                        name = "Main",
                        publicKey = "a12b37d24383bb22512dab5d4f0c4e3053efc6b85b4f5b677634c997b5a7e8db",
                        secretKey = "af984cfa6f53a505f91bbe9cf86b230fb15bab36b6fe87baf3954aacb9af8238a12b37d24383bb22512dab5d4f0c4e3053efc6b85b4f5b677634c997b5a7e8db"
                    ),
                    MainWallet(
                        name = "Main 2",
                        publicKey = "7deb4e00a53cdc4c21a6806259978c5b93bc5e690b8c5dbae1877880ddb06c25",
                        secretKey = "1968fdb9144970654f3c3bcec5538361c663bafecf8b35bc915abe66607303ce7deb4e00a53cdc4c21a6806259978c5b93bc5e690b8c5dbae1877880ddb06c25"
                    )
                )
            )
            TOKEN_TRUST -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+atm_user_2mainwallet@gmail.com",
                "NB25SEJL6KXXMNK4",
                listOf(
                    MainWallet(
                        name = "Main 1",
                        publicKey = "cfb81cfd6d17e79424a3deaa40c2e04ea1f2cb30132b02a94709f84b172f9a9d",
                        secretKey = "8b5e3c0d196ee502941ca6919a085951a975b77a4a4c914ee5e090fa3e6eed33cfb81cfd6d17e79424a3deaa40c2e04ea1f2cb30132b02a94709f84b172f9a9d"
                    ),
                    MainWallet(
                        name = "Main 2",
                        publicKey = "a332b1e0509f764c0d651d1f926a67e5e16839154c13f9ab635a9d4d49f2ea75",
                        secretKey = "5da329b9ad57ccf980e1092573a230931f3ee38c5385f931ec1d015d7d1f89eaa332b1e0509f764c0d651d1f926a67e5e16839154c13f9ab635a9d4d49f2ea75"
                    )
                )
            )
        }

        // Main, OTF, autotest Move
        val ATM_USER_MAIN_OTF_MOVE = when (Environment.stand) {
            DEVELOP -> UserWithMainWalletAndOtf(
                "",
                MainWallet(),
                OtfWallet()
            )
            RELEASE -> UserWithMainWalletAndOtf(
                "aft.uat.sdex+atm_main_otf_transfer_1@gmail.com",
                MainWallet(
                    name = "Main 1",
                    publicKey = "4bebe53a3c2ce9ba8ca35ef88a2b8e68d7fefa21425dd2dcbd6e08a70c872030",
                    secretKey = "df4d175abc0b93ae16ea62bbdc8e19a2c39e5bae90c039577c2b96b101278fb24bebe53a3c2ce9ba8ca35ef88a2b8e68d7fefa21425dd2dcbd6e08a70c872030"
                ),
                OtfWallet(
                    name = "OTF 1",
                    publicKey = "ec28621b0816aabcc15653a365b6dcf47de9263f805b72b92be9af0b2c36cfc1",
                    secretKey = "42aeecc13638c5efa40ecf2904d29989600e4f35013112c3fb492dc7894bf20dec28621b0816aabcc15653a365b6dcf47de9263f805b72b92be9af0b2c36cfc1"
                )
            )
            PREPROD -> UserWithMainWalletAndOtf(
                "aft.uat.sdex+atm_main_otf_transfer_2@gmail.com",
                MainWallet(
                    publicKey = "84745e2ff18393711ec319ff9a6fa2e1fb6e22baaf430799cbc2ea804ffdc152",
                    secretKey = "e458a35d253967313ff60e4f4d556cee16d2f54e028054cd36f4e121c2690d4684745e2ff18393711ec319ff9a6fa2e1fb6e22baaf430799cbc2ea804ffdc152"
                ),
                OtfWallet(
                    publicKey = "7914e6ade33f4b2a22a682a781c1608bbb36495d05fec013d8e6d0322fe4978d",
                    secretKey = "c5f8e6bf4389b64f013700dc4bd43c0a98181a9c55ea383d8e85b48f23cf61237914e6ade33f4b2a22a682a781c1608bbb36495d05fec013d8e6d0322fe4978d"
                )
            )
            PROD -> UserWithMainWalletAndOtf(
                "", MainWallet(),
                OtfWallet()
            )
            SHARED -> UserWithMainWalletAndOtf(
                "",
                MainWallet(),
                OtfWallet()
            )
            TOKEN_TRUST -> UserWithMainWalletAndOtf(
                "aft.uat.sdex+atm_main_otf_transfer@gmail.com",
                MainWallet(
                    name = "Main 1",
                    publicKey = "7739cb50ee309f767a90c151d75150ab73fdb155760172cefd2775d5cfbda48a",
                    secretKey = "08e255688e87fe108a6ab4f2dc20f7028e59e5bb3675856eefa651a62fa4b0b17739cb50ee309f767a90c151d75150ab73fdb155760172cefd2775d5cfbda48a"
                ),
                OtfWallet(
                    publicKey = "18c60f468408b1bb09b50a0b1073cc7411f8ed5f41469ea8fadd874f6069dfd9",
                    secretKey = "e106704d64da08d7a3ba5e87c22fc16e097b7514c959e0958fddb89a5114e9db18c60f468408b1bb09b50a0b1073cc7411f8ed5f41469ea8fadd874f6069dfd9"
                )
            )
        }

        // Atm user: OAuth, Main, OTF, autotest Move
        val ATM_USER_MAIN_OAUTH_OTF_MOVE = when (Environment.stand) {
            DEVELOP -> UserWithMainWalletAndOtf2FA(
                "",
                "",
                MainWallet(),
                OtfWallet()
            )
            RELEASE -> UserWithMainWalletAndOtf2FA(
                "aft.uat.sdex+atm_oauth_main_otf_transfer@gmail.com",
                "BXVC5XWWJB46J2PO",
                MainWallet(
                    publicKey = "a9a4e2e5682d9179f0ddc9269759236429e95e3fb1753a7e6b55888f4ffe7d51",
                    secretKey = "5c69853be3fcd97fbefb83aafd0793ebec8466cc4657fcf4b78956ec93febceda9a4e2e5682d9179f0ddc9269759236429e95e3fb1753a7e6b55888f4ffe7d51"
                ),
                OtfWallet(
                    publicKey = "de0f52358d7477afda8285fcacc76af84b5fc0220aab7a59bf6280cb354dcfe0",
                    secretKey = "9ccda14d6f53e669bb62f727a1ef8d1421033f23337133f728ec1ae235be3725de0f52358d7477afda8285fcacc76af84b5fc0220aab7a59bf6280cb354dcfe0"
                )
            )
            PREPROD -> UserWithMainWalletAndOtf2FA(
                "aft.uat.sdex+atm_oauth_main_otf_transfer_1@gmail.com",
                "Q2UZDD4ITHHUK3WH",
                MainWallet(
                    publicKey = "67de61f660dcf15ac0a948e2e8f7b369bf672fb0e38dc0bb7043e652f2787405",
                    secretKey = "3d6f4ba682f27ff952b0f7e31c809ca6a4a905bf8cd87fc37dcfbf2d0ddea0c367de61f660dcf15ac0a948e2e8f7b369bf672fb0e38dc0bb7043e652f2787405"
                ),
                OtfWallet(
                    publicKey = "7914e6ade33f4b2a22a682a781c1608bbb36495d05fec013d8e6d0322fe4978d",
                    secretKey = "3da11fb7b83a80ff599407dcac071b04fd86e49cd9d17fd84e330798c04c48d19423d7e86e4c18c4c6675a326188fea5f28cf558a798e3130d185b5f567aed90"
                )
            )
            PROD -> UserWithMainWalletAndOtf2FA(
                "",
                "",
                MainWallet(),
                OtfWallet()
            )
            SHARED -> UserWithMainWalletAndOtf2FA(
                "",
                "",
                MainWallet(),
                OtfWallet()
            )
            TOKEN_TRUST -> UserWithMainWalletAndOtf2FA(
                "aft.uat.sdex+atm_oauth_main_otf_transfer@gmail.com",
                "TXI7JTCUK3OOM2FR",
                MainWallet(
                    name = "Main 1",
                    publicKey = "a35e6b0e3f83782eda7689bf8993934cab195e5c363350b7a2a213803214393b",
                    secretKey = "37993824d38ff707a559ac7ccbafbde1c6967aa1bbaeb319c17c44b24e8b98ada35e6b0e3f83782eda7689bf8993934cab195e5c363350b7a2a213803214393b"
                ),
                OtfWallet(
                    publicKey = "497ea57a9416b5c34041aed2496ef4154b078cfeacddb430212ddca646be0cbb",
                    secretKey = "1962e76a1892f92e52eec2f2fc61533f8fad3b1ea524d1571b32b5e0b1dfd8e0497ea57a9416b5c34041aed2496ef4154b078cfeacddb430212ddca646be0cbb"
                )

            )
        }

        // Atm user: OAuth, Main, OTF, autotestTransfer
        val ATM_USER_2FA_MAIN_OTF_TRANSFER = when (Environment.stand) {
            DEVELOP -> UserWithMainWallet2FA(
                mainWallet = MainWallet(),
                oAuthSecret = ""
            )
            RELEASE -> UserWithMainWallet2FA(
                mainWallet = MainWallet(),
                oAuthSecret = ""
            )
            PREPROD -> UserWithMainWallet2FA(
                "aft.uat.sdex+atm_user_2mainwallet@gmail.com",
                "TE5TQ3XCTPCFNFAT",
                mainWallet = MainWallet(
                    publicKey = "404eb1c6b32188f038f8a50e0d7b5bb58e676bcadf3f35453068c526c087c1ef",
                    secretKey = "8b0531191c5bde5dc7556c36045a4897983ce36a994efff121866050cce4e193404eb1c6b32188f038f8a50e0d7b5bb58e676bcadf3f35453068c526c087c1ef"
                )
            )
            PROD -> UserWithMainWallet2FA(
                mainWallet = MainWallet(),
                oAuthSecret = ""
            )
            SHARED -> UserWithMainWallet2FA(
                mainWallet = MainWallet(),
                oAuthSecret = ""
            )
            TOKEN_TRUST -> UserWithMainWallet2FA(
                mainWallet = MainWallet(),
                oAuthSecret = ""
            )
        }

        // Atm user: OAuth, Main, OTF, autotestTransfer
        val ATM_USER_MAIN_OTF_TRANSFER = when (Environment.stand) {
            DEVELOP -> UserWithOtfWallet2FA(
                oAuthSecret = "",
                otfWallet = OtfWallet()
            )
            RELEASE -> UserWithOtfWallet2FA(
                "aft.uat.sdex+atm_user_2mainwallet@gmail.com",
                oAuthSecret = "LIG3FRMOMSG5S7A3",
                otfWallet = OtfWallet(
                    publicKey = "92086071d054f91bf6768a89ae34096b3ff45752d215727e4ba932cbe44f78ef",
                    secretKey = "33bcb3cea0c62c527e8d0db4b8775955b667cceb9ac9b2597fb48c7fd8e90ac092086071d054f91bf6768a89ae34096b3ff45752d215727e4ba932cbe44f78ef"
                )
            )
            PREPROD -> UserWithOtfWallet2FA(
                email = "aft.uat.sdex+atm_user_2mainwallet@gmail.com",
                oAuthSecret = "TE5TQ3XCTPCFNFAT",
                otfWallet = OtfWallet(
                    publicKey = "404eb1c6b32188f038f8a50e0d7b5bb58e676bcadf3f35453068c526c087c1ef",
                    secretKey = "8b0531191c5bde5dc7556c36045a4897983ce36a994efff121866050cce4e193404eb1c6b32188f038f8a50e0d7b5bb58e676bcadf3f35453068c526c087c1ef"
                )
            )
            PROD -> UserWithOtfWallet2FA(
                oAuthSecret = "",
                otfWallet = OtfWallet()
            )
            SHARED -> UserWithOtfWallet2FA(
                oAuthSecret = "",
                otfWallet = OtfWallet()
            )
            TOKEN_TRUST -> UserWithOtfWallet2FA(
                oAuthSecret = "",
                otfWallet = OtfWallet()
            )
        }

        val ATM_USER_MAIN_FOR_IT_ONE = when (Environment.stand) {
            DEVELOP -> UserWithMainWalletAndOtf(
                "",
                MainWallet(),
                OtfWallet()
            )
            RELEASE -> UserWithMainWalletAndOtf(
                "aft.uat.sdex+atm_main_for_it_one@gmail.com",
                MainWallet(
                    name = "Main 1",
                    publicKey = "47d8d24e9e486b5d297191ac47efb5b03a5c1794327107d9456c8c0e81ad4fce",
                    secretKey = "e1084bda3d862d58e2e8e9391d91a7a8940705ee1bfd51fcce6940d5bd1fb9ba47d8d24e9e486b5d297191ac47efb5b03a5c1794327107d9456c8c0e81ad4fce"
                ),
                OtfWallet(
                    name = "",
                    publicKey = "",
                    secretKey = ""
                )
            )
            PREPROD -> UserWithMainWalletAndOtf(
                "aft.uat.sdex+atm_main_for_it_one@gmail.com",
                MainWallet(
                    name = "Main 1",
                    publicKey = "ca1aa564ea87f9ab68972cdf454f36ac2b3a621219bb6f527f1cd72424ded1d9",
                    secretKey = "c1a3dcdcd4d27b12f6d69ce98be0deb188098e9f40ea21885c1aed30f39f7ac0ca1aa564ea87f9ab68972cdf454f36ac2b3a621219bb6f527f1cd72424ded1d9"
                ),
                OtfWallet(
                    publicKey = "",
                    secretKey = ""
                )
            )
            PROD -> UserWithMainWalletAndOtf(
                "", MainWallet(),
                OtfWallet()
            )
            SHARED -> UserWithMainWalletAndOtf(
                "",
                MainWallet(),
                OtfWallet()
            )
            TOKEN_TRUST -> UserWithMainWalletAndOtf(
                "aft.uat.sdex+atm_main_otf_transfer@gmail.com",
                MainWallet(
                    name = "Main 1",
                    publicKey = "7739cb50ee309f767a90c151d75150ab73fdb155760172cefd2775d5cfbda48a",
                    secretKey = "08e255688e87fe108a6ab4f2dc20f7028e59e5bb3675856eefa651a62fa4b0b17739cb50ee309f767a90c151d75150ab73fdb155760172cefd2775d5cfbda48a"
                ),
                OtfWallet(
                    publicKey = "18c60f468408b1bb09b50a0b1073cc7411f8ed5f41469ea8fadd874f6069dfd9",
                    secretKey = "e106704d64da08d7a3ba5e87c22fc16e097b7514c959e0958fddb89a5114e9db18c60f468408b1bb09b50a0b1073cc7411f8ed5f41469ea8fadd874f6069dfd9"
                )
            )
        }

        val ATM_USER_MAIN_FOR_IT_SECOND = when (Environment.stand) {
            DEVELOP -> UserWithMainWalletAndOtf(
                "",
                MainWallet(),
                OtfWallet()
            )
            RELEASE -> UserWithMainWalletAndOtf(
                "aft.uat.sdex+atm_main_for_it_second@gmail.com",
                MainWallet(
                    name = "Main 1",
                    publicKey = "5d2ada3c1966ad35d446c3030f2e526285d9ca2a662cd76a0be8bb0de73e3209",
                    secretKey = "5dd3a9ac98d4166f565a493dc5b03e2e13051fb028ebe19468fad07485cecf2d5d2ada3c1966ad35d446c3030f2e526285d9ca2a662cd76a0be8bb0de73e3209"
                ),
                OtfWallet(
                    name = "OTF 1",
                    publicKey = "",
                    secretKey = ""
                )
            )
            PREPROD -> UserWithMainWalletAndOtf(
                "aft.uat.sdex+atm_main_for_it_second@gmail.com",
                MainWallet(
                    name = "Main 1",
                    publicKey = "e013bb5b1bf301254d2be29ea9a495342ae0f9a496e65e4ccc2566b1365aab70",
                    secretKey = "efae50309eb224b74466206014cca6b12fab5a3bb1c2d3d7764e433d277a343ce013bb5b1bf301254d2be29ea9a495342ae0f9a496e65e4ccc2566b1365aab70"
                ),
                OtfWallet(
                    publicKey = "",
                    secretKey = ""
                )
            )
            PROD -> UserWithMainWalletAndOtf(
                "", MainWallet(),
                OtfWallet()
            )
            SHARED -> UserWithMainWalletAndOtf(
                "",
                MainWallet(),
                OtfWallet()
            )
            TOKEN_TRUST -> UserWithMainWalletAndOtf(
                "",
                MainWallet(
                    name = "Main 1",
                    publicKey = "",
                    secretKey = ""
                ),
                OtfWallet(
                    publicKey = "",
                    secretKey = ""
                )
            )
        }

        val ATM_USER_MAIN_FOR_IT_THIRD = when (Environment.stand) {
            DEVELOP -> UserWithMainWalletAndOtf(
                "",
                MainWallet(),
                OtfWallet()
            )
            RELEASE -> UserWithMainWalletAndOtf(
                "aft.uat.sdex+atm_main_for_it_third@gmail.com",
                MainWallet(
                    name = "Main 1",
                    publicKey = "b36b1fab7ed764131717935bf4ce61e7fd9d3861b515d81bed05523a9a5bc088",
                    secretKey = "2cdc97630f5b74a03b48cb7fc003ee8867c1e617a20d5e9391aa8ce491ee418bb36b1fab7ed764131717935bf4ce61e7fd9d3861b515d81bed05523a9a5bc088"
                ),
                OtfWallet(
                    name = "",
                    publicKey = "",
                    secretKey = ""
                )
            )
            PREPROD -> UserWithMainWalletAndOtf(
                "aft.uat.sdex+atm_main_for_it_third@gmail.com",
                MainWallet(
                    name = "Main 1",
                    publicKey = "bd4d655255c07ece79faf6db6c18efea92b47f362b8410452888463fb325d118",
                    secretKey = "98c8f9985657bc63e64507d79c20aced1bf32f3ca7d63ec38c0551d43c0e0a31bd4d655255c07ece79faf6db6c18efea92b47f362b8410452888463fb325d118"
                ),
                OtfWallet(
                    publicKey = "",
                    secretKey = ""
                )
            )
            PROD -> UserWithMainWalletAndOtf(
                "", MainWallet(),
                OtfWallet()
            )
            SHARED -> UserWithMainWalletAndOtf(
                "",
                MainWallet(),
                OtfWallet()
            )
            TOKEN_TRUST -> UserWithMainWalletAndOtf(
                "",
                MainWallet(
                    name = "Main 1",
                    publicKey = "",
                    secretKey = ""
                ),
                OtfWallet(
                    publicKey = "",
                    secretKey = ""
                )
            )
        }

        /**Пользователи с припиской ACCEPT_какой то токен_TOKENS после заведения используют ключ из данного файл
        это пользователи для Аппрува Деклайна
        https://sdexnt.atlassian.net/wiki/spaces/ATM/pages/1744896011
        Atm user: CC VT IT Issuer название компании для релиза
        Atm user: RomanovShortname название компании для препрода
        Для того чтобы можно было использовать пользователя Issuer ему также необходимо
        после заведения в том или ином кошельке для определьнного токена проставить роль Controller*/

        //for work with IT token
        val ATM_USER_FOR_ACCEPT_CCVTIT_TOKENS = when (Environment.stand) {
            DEVELOP -> UserWithMultipleMainWallet2FA(
                "",
                "",
                listOf()
            )
            RELEASE -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+for_accept_token@gmail.com",
                "",
                listOf(
                    MainWallet(
                        name = "",
                        publicKey = "",
                        secretKey = "d8937be1d83f73abb68beae10d99bccb481704a9a075932e162c00039fd8fd9808856efbbbc44c4b5476596da89cf500266c789ce054a221a1eaa7fa41463745"
                    )
                )
            )
            PREPROD -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+for_accept_tokens@gmail.com",
                "",
                listOf(
                    MainWallet(
                        name = "",
                        publicKey = "08856efbbbc44c4b5476596da89cf500266c789ce054a221a1eaa7fa41463745",
                        secretKey = "d8937be1d83f73abb68beae10d99bccb481704a9a075932e162c00039fd8fd9808856efbbbc44c4b5476596da89cf500266c789ce054a221a1eaa7fa41463745"
                    ),
                    //для FT
                    MainWallet(
                        name = "",
                        publicKey = "974a32f42be7b72d735d80843106d87add11c5b107b6e2429dea43a1250d4a2b",
                        secretKey = "6fb7f9ad0c307d8fa80a5e9918002c9dbb066eb14e7175fde647cd0e58a8a5de974a32f42be7b72d735d80843106d87add11c5b107b6e2429dea43a1250d4a2b"
                    )
                )
            )
            PROD -> UserWithMultipleMainWallet2FA(
                "",
                "",
                listOf()
            )
            SHARED -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+for_accept_tokens@gmail.com",
                "",
                listOf(
                    MainWallet(
                        name = "",
                        publicKey = "",
                        secretKey = "d8937be1d83f73abb68beae10d99bccb481704a9a075932e162c00039fd8fd9808856efbbbc44c4b5476596da89cf500266c789ce054a221a1eaa7fa41463745"
                    )
                )
            )
            TOKEN_TRUST -> UserWithMultipleMainWallet2FA(
                "",
                "",
                listOf()
            )
        }

        //for work with IT token
        val ATM_USER_FOR_ACCEPT_IT_TOKEN_ONE = when (Environment.stand) {
            DEVELOP -> UserWithMultipleMainWallet2FA(
                "",
                "",
                listOf()
            )
            RELEASE -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+for_accept_tokens_one@gmail.com",
                "",
                listOf(
                    MainWallet(
                        name = "",
                        publicKey = "",
                        secretKey = "d8937be1d83f73abb68beae10d99bccb481704a9a075932e162c00039fd8fd9808856efbbbc44c4b5476596da89cf500266c789ce054a221a1eaa7fa41463745"
                    )
                )
            )
            PREPROD -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+for_accept_token_one@gmail.com",
                "",
                listOf(
                    MainWallet(
                        name = "",
                        publicKey = "08856efbbbc44c4b5476596da89cf500266c789ce054a221a1eaa7fa41463745",
                        secretKey = "d8937be1d83f73abb68beae10d99bccb481704a9a075932e162c00039fd8fd9808856efbbbc44c4b5476596da89cf500266c789ce054a221a1eaa7fa41463745"
                    )
                )
            )
            PROD -> UserWithMultipleMainWallet2FA(
                "",
                "",
                listOf()
            )
            SHARED -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+for_accept_tokens@gmail.com",
                "",
                listOf(
                    MainWallet(
                        name = "",
                        publicKey = "",
                        secretKey = "d8937be1d83f73abb68beae10d99bccb481704a9a075932e162c00039fd8fd9808856efbbbc44c4b5476596da89cf500266c789ce054a221a1eaa7fa41463745"
                    )
                )
            )
            TOKEN_TRUST -> UserWithMultipleMainWallet2FA(
                "",
                "",
                listOf()
            )
        }

        //for work with IT token
        val ATM_USER_FOR_ACCEPT_IT_TOKEN_SECOND = when (Environment.stand) {
            DEVELOP -> UserWithMultipleMainWallet2FA(
                "",
                "",
                listOf()
            )
            RELEASE -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+for_accept_token_second@gmail.com",
                "",
                listOf(
                    MainWallet(
                        name = "",
                        publicKey = "",
                        secretKey = "d8937be1d83f73abb68beae10d99bccb481704a9a075932e162c00039fd8fd9808856efbbbc44c4b5476596da89cf500266c789ce054a221a1eaa7fa41463745"
                    )
                )
            )
            PREPROD -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+for_accept_token_second@gmail.com",
                "",
                listOf(
                    MainWallet(
                        name = "",
                        publicKey = "08856efbbbc44c4b5476596da89cf500266c789ce054a221a1eaa7fa41463745",
                        secretKey = "d8937be1d83f73abb68beae10d99bccb481704a9a075932e162c00039fd8fd9808856efbbbc44c4b5476596da89cf500266c789ce054a221a1eaa7fa41463745"
                    )
                )
            )
            PROD -> UserWithMultipleMainWallet2FA(
                "",
                "",
                listOf()
            )
            SHARED -> UserWithMultipleMainWallet2FA(
                "",
                "",
                listOf(
                    MainWallet(
                        name = "",
                        publicKey = "",
                        secretKey = ""
                    )
                )
            )
            TOKEN_TRUST -> UserWithMultipleMainWallet2FA(
                "",
                "",
                listOf()
            )
        }

        //for work with IT token
        val ATM_USER_FOR_ACCEPT_IT_TOKEN_THIRD = when (Environment.stand) {
            DEVELOP -> UserWithMultipleMainWallet2FA(
                "",
                "",
                listOf()
            )
            RELEASE -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+for_accept_token_third@gmail.com",
                "",
                listOf(
                    MainWallet(
                        name = "",
                        publicKey = "",
                        secretKey = "d8937be1d83f73abb68beae10d99bccb481704a9a075932e162c00039fd8fd9808856efbbbc44c4b5476596da89cf500266c789ce054a221a1eaa7fa41463745"
                    )
                )
            )
            PREPROD -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+for_accept_tokens_third@gmail.com",
                "",
                listOf(
                    MainWallet(
                        name = "",
                        publicKey = "08856efbbbc44c4b5476596da89cf500266c789ce054a221a1eaa7fa41463745",
                        secretKey = "d8937be1d83f73abb68beae10d99bccb481704a9a075932e162c00039fd8fd9808856efbbbc44c4b5476596da89cf500266c789ce054a221a1eaa7fa41463745"
                    )
                )
            )
            PROD -> UserWithMultipleMainWallet2FA(
                "",
                "",
                listOf()
            )
            SHARED -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+for_accept_tokens@gmail.com",
                "",
                listOf(
                    MainWallet(
                        name = "",
                        publicKey = "",
                        secretKey = "d8937be1d83f73abb68beae10d99bccb481704a9a075932e162c00039fd8fd9808856efbbbc44c4b5476596da89cf500266c789ce054a221a1eaa7fa41463745"
                    )
                )
            )
            TOKEN_TRUST -> UserWithMultipleMainWallet2FA(
                "",
                "",
                listOf()
            )
        }

        //for work with FT token
        val ATM_USER_FOR_ACCEPT_FT_TOKENS = when (Environment.stand) {
            DEVELOP -> UserWithMultipleMainWallet2FA(
                "",
                "",
                listOf()
            )
            RELEASE -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+for_accept_ft_token@gmail.com",
                "",
                listOf(
                    MainWallet(
                        name = "FT_ISSUER_WALLET",
                        publicKey = "974a32f42be7b72d735d80843106d87add11c5b107b6e2429dea43a1250d4a2b",
                        secretKey = "6fb7f9ad0c307d8fa80a5e9918002c9dbb066eb14e7175fde647cd0e58a8a5de974a32f42be7b72d735d80843106d87add11c5b107b6e2429dea43a1250d4a2b"
                    )
                )
            )
            PREPROD -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+for_accept_tokens@gmail.com",
                "",
                listOf(
                    MainWallet(
                        name = "",
                        publicKey = "",
                        secretKey = ""
                    )
                )
            )
            PROD -> UserWithMultipleMainWallet2FA(
                "",
                "",
                listOf()
            )
            SHARED -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+for_accept_tokens@gmail.com",
                "",
                listOf(
                    MainWallet(
                        name = "",
                        publicKey = "",
                        secretKey = "6fb7f9ad0c307d8fa80a5e9918002c9dbb066eb14e7175fde647cd0e58a8a5de974a32f42be7b72d735d80843106d87add11c5b107b6e2429dea43a1250d4a2b"
                    )
                )
            )
            TOKEN_TRUST -> UserWithMultipleMainWallet2FA(
                "",
                "",
                listOf()
            )
        }

        //for work with ETC token
        val ATM_USER_FOR_ACCEPT_ETC_TOKENS_WITHOUT_2FA = when (Environment.stand) {
            DEVELOP -> UserWithMultipleMainWallet2FA(
                "",
                "",
                listOf()
            )
            RELEASE -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+for_accept_etc_tokens@gmail.com",
                "",
                listOf(
                    MainWallet(
                        name = "ETC Issuer",
                        publicKey = "0f531508888a7bbf2c6a14df441a14401d8192df3f7c93b05d08115298565189",
                        secretKey = "4d14d1db55d4612ac2e0d2369e75120b1b46dde5b9a3b8ede4231f63e37d3ca30f531508888a7bbf2c6a14df441a14401d8192df3f7c93b05d08115298565189"
                    ),
                    MainWallet(
                        name = "redeem etc issuer",
                        publicKey = "441a504f50bca26a5b37e877c8101a593751438f8bca5f22f811aab39d30fe37",
                        secretKey = "f031816f8ee8f4c89e31dd66046ab22111e0bf23b172969759d62bd5aac6544d441a504f50bca26a5b37e877c8101a593751438f8bca5f22f811aab39d30fe37"
                    )
                )
            )
            PREPROD -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+for_accept_etc_tokens@gmail.com",
                "",
                listOf(
                    MainWallet(
                        name = "Issuer wallet - ETC",
                        publicKey = "0f531508888a7bbf2c6a14df441a14401d8192df3f7c93b05d08115298565189",
                        secretKey = "4d14d1db55d4612ac2e0d2369e75120b1b46dde5b9a3b8ede4231f63e37d3ca30f531508888a7bbf2c6a14df441a14401d8192df3f7c93b05d08115298565189"
                    ),
                    MainWallet(
                        name = "Issuer wallet - ETC redeem",
                        publicKey = "441a504f50bca26a5b37e877c8101a593751438f8bca5f22f811aab39d30fe37",
                        secretKey = "f031816f8ee8f4c89e31dd66046ab22111e0bf23b172969759d62bd5aac6544d441a504f50bca26a5b37e877c8101a593751438f8bca5f22f811aab39d30fe37"
                    )
                )
            )
            PROD -> UserWithMultipleMainWallet2FA(
                "",
                "",
                listOf()
            )
            //TODO создать
            SHARED -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+for_accept_etc_tokens@gmail.com",
                "",
                listOf(
                    MainWallet(
                        name = "",
                        publicKey = "",
                        secretKey = "d8937be1d83f73abb68beae10d99bccb481704a9a075932e162c00039fd8fd9808856efbbbc44c4b5476596da89cf500266c789ce054a221a1eaa7fa41463745"
                    )
                )
            )
            //TODO создать
            TOKEN_TRUST -> UserWithMultipleMainWallet2FA(
                "",
                "",
                listOf()
            )
        }

        //for work with ETC token
        val ATM_USER_FOR_ACCEPT_ETC_TOKENS_THIRD = when (Environment.stand) {
            DEVELOP -> UserWithMultipleMainWallet2FA(
                "",
                "",
                listOf()
            )
            RELEASE -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+for_accept_etc_tokens_three@gmail.com",
                "",
                listOf(
                    MainWallet(
                        name = "ETC Issuer",
                        publicKey = "0f531508888a7bbf2c6a14df441a14401d8192df3f7c93b05d08115298565189",
                        secretKey = "4d14d1db55d4612ac2e0d2369e75120b1b46dde5b9a3b8ede4231f63e37d3ca30f531508888a7bbf2c6a14df441a14401d8192df3f7c93b05d08115298565189"
                    ),
                    MainWallet(
                        name = "redeem etc issuer",
                        publicKey = "441a504f50bca26a5b37e877c8101a593751438f8bca5f22f811aab39d30fe37",
                        secretKey = "f031816f8ee8f4c89e31dd66046ab22111e0bf23b172969759d62bd5aac6544d441a504f50bca26a5b37e877c8101a593751438f8bca5f22f811aab39d30fe37"
                    )
                )
            )
            //TODO создать
            PREPROD -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+for_accept_etc_tokens_three@gmail.com",
                "",
                listOf(
                    MainWallet(
                        name = "Issuer wallet - ETC",
                        publicKey = "0f531508888a7bbf2c6a14df441a14401d8192df3f7c93b05d08115298565189",
                        secretKey = "4d14d1db55d4612ac2e0d2369e75120b1b46dde5b9a3b8ede4231f63e37d3ca30f531508888a7bbf2c6a14df441a14401d8192df3f7c93b05d08115298565189"
                    ),
                    MainWallet(
                        name = "Issuer wallet - ETC redeem",
                        publicKey = "441a504f50bca26a5b37e877c8101a593751438f8bca5f22f811aab39d30fe37",
                        secretKey = "f031816f8ee8f4c89e31dd66046ab22111e0bf23b172969759d62bd5aac6544d441a504f50bca26a5b37e877c8101a593751438f8bca5f22f811aab39d30fe37"
                    )
                )
            )
            PROD -> UserWithMultipleMainWallet2FA(
                "",
                "",
                listOf()
            )
            //TODO создать
            SHARED -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+for_accept_etc_tokens@gmail.com",
                "",
                listOf(
                    MainWallet(
                        name = "",
                        publicKey = "",
                        secretKey = "d8937be1d83f73abb68beae10d99bccb481704a9a075932e162c00039fd8fd9808856efbbbc44c4b5476596da89cf500266c789ce054a221a1eaa7fa41463745"
                    )
                )
            )
            //TODO создать
            TOKEN_TRUST -> UserWithMultipleMainWallet2FA(
                "",
                "",
                listOf()
            )
        }

        //for work with ETC token
        val ATM_USER_FOR_ACCEPT_ETC_TOKENS_SECOND = when (Environment.stand) {
            DEVELOP -> UserWithMultipleMainWallet2FA(
                "",
                "",
                listOf()
            )
            RELEASE -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+for_accept_etc_tokens_two@gmail.com",
                "",
                listOf(
                    MainWallet(
                        name = "ETC Issuer",
                        publicKey = "0f531508888a7bbf2c6a14df441a14401d8192df3f7c93b05d08115298565189",
                        secretKey = "4d14d1db55d4612ac2e0d2369e75120b1b46dde5b9a3b8ede4231f63e37d3ca30f531508888a7bbf2c6a14df441a14401d8192df3f7c93b05d08115298565189"
                    ),
                    MainWallet(
                        name = "redeem etc issuer",
                        publicKey = "441a504f50bca26a5b37e877c8101a593751438f8bca5f22f811aab39d30fe37",
                        secretKey = "f031816f8ee8f4c89e31dd66046ab22111e0bf23b172969759d62bd5aac6544d441a504f50bca26a5b37e877c8101a593751438f8bca5f22f811aab39d30fe37"
                    )
                )
            )
            PREPROD -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+for_accept_etc_tokens_two@gmail.com",
                "",
                listOf(
                    MainWallet(
                        name = "Issuer wallet - ETC",
                        publicKey = "0f531508888a7bbf2c6a14df441a14401d8192df3f7c93b05d08115298565189",
                        secretKey = "4d14d1db55d4612ac2e0d2369e75120b1b46dde5b9a3b8ede4231f63e37d3ca30f531508888a7bbf2c6a14df441a14401d8192df3f7c93b05d08115298565189"
                    ),
                    MainWallet(
                        name = "Issuer wallet - ETC redeem",
                        publicKey = "441a504f50bca26a5b37e877c8101a593751438f8bca5f22f811aab39d30fe37",
                        secretKey = "f031816f8ee8f4c89e31dd66046ab22111e0bf23b172969759d62bd5aac6544d441a504f50bca26a5b37e877c8101a593751438f8bca5f22f811aab39d30fe37"
                    )
                )
            )
            PROD -> UserWithMultipleMainWallet2FA(
                "",
                "",
                listOf()
            )
            //TODO создать
            SHARED -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+for_accept_etc_tokens@gmail.com",
                "",
                listOf(
                    MainWallet(
                        name = "",
                        publicKey = "",
                        secretKey = "d8937be1d83f73abb68beae10d99bccb481704a9a075932e162c00039fd8fd9808856efbbbc44c4b5476596da89cf500266c789ce054a221a1eaa7fa41463745"
                    )
                )
            )
            //TODO создать
            TOKEN_TRUST -> UserWithMultipleMainWallet2FA(
                "",
                "",
                listOf()
            )
        }

        //for work with ETC token
        val ATM_USER_FOR_ACCEPT_ETC_TOKENS_2FA = when (Environment.stand) {
            DEVELOP -> UserWithMultipleMainWallet2FA(
                "",
                "",
                listOf()
            )
            RELEASE -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+for_accept_etc_tokens_2fa@gmail.com",
                "CU6673YZZJGWLBQB",
                listOf(
                    MainWallet(
                        name = "ETC Issuer",
                        publicKey = "0f531508888a7bbf2c6a14df441a14401d8192df3f7c93b05d08115298565189",
                        secretKey = "4d14d1db55d4612ac2e0d2369e75120b1b46dde5b9a3b8ede4231f63e37d3ca30f531508888a7bbf2c6a14df441a14401d8192df3f7c93b05d08115298565189"
                    ),
                    MainWallet(
                        name = "redeem etc issuer",
                        publicKey = "441a504f50bca26a5b37e877c8101a593751438f8bca5f22f811aab39d30fe37",
                        secretKey = "f031816f8ee8f4c89e31dd66046ab22111e0bf23b172969759d62bd5aac6544d441a504f50bca26a5b37e877c8101a593751438f8bca5f22f811aab39d30fe37"
                    )
                )
            )
            PREPROD -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+for_accept_etc_tokens_2fa@gmail.com",
                "SALMOUCBBJG5LLD7",
                listOf(
                    MainWallet(
                        name = "Issuer wallet - ETC",
                        publicKey = "0f531508888a7bbf2c6a14df441a14401d8192df3f7c93b05d08115298565189",
                        secretKey = "4d14d1db55d4612ac2e0d2369e75120b1b46dde5b9a3b8ede4231f63e37d3ca30f531508888a7bbf2c6a14df441a14401d8192df3f7c93b05d08115298565189"
                    ),
                    MainWallet(
                        name = "Issuer wallet - ETC redeem",
                        publicKey = "441a504f50bca26a5b37e877c8101a593751438f8bca5f22f811aab39d30fe37",
                        secretKey = "f031816f8ee8f4c89e31dd66046ab22111e0bf23b172969759d62bd5aac6544d441a504f50bca26a5b37e877c8101a593751438f8bca5f22f811aab39d30fe37"
                    )
                )
            )
            PROD -> UserWithMultipleMainWallet2FA(
                "",
                "",
                listOf()
            )
            //TODO создать
            SHARED -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+for_accept_etc_tokens_2fa@gmail.com",
                "",
                listOf(
                    MainWallet(
                        name = "",
                        publicKey = "",
                        secretKey = "d8937be1d83f73abb68beae10d99bccb481704a9a075932e162c00039fd8fd9808856efbbbc44c4b5476596da89cf500266c789ce054a221a1eaa7fa41463745"
                    )
                )
            )
            TOKEN_TRUST -> UserWithMultipleMainWallet2FA(
                "",
                "",
                listOf()
            )
        }

        //for work with ETC token не контроллер будь внимательнее
        val ATM_USER_FOR_ACCEPT_ETC_TOKENS_NOT_CONTROLLER = when (Environment.stand) {
            DEVELOP -> UserWithMultipleMainWallet2FA(
                "",
                "",
                listOf()
            )
            RELEASE -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+for_accept_etc_tokens_not_controller@gmail.com",
                "",
                listOf(
                    MainWallet(
                        name = "ETC Issuer",
                        publicKey = "0f531508888a7bbf2c6a14df441a14401d8192df3f7c93b05d08115298565189",
                        secretKey = "4d14d1db55d4612ac2e0d2369e75120b1b46dde5b9a3b8ede4231f63e37d3ca30f531508888a7bbf2c6a14df441a14401d8192df3f7c93b05d08115298565189"
                    )
                )
            )
            PREPROD -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+for_accept_etc_tokens_not_controller@gmail.com",
                "",
                listOf(
                    MainWallet(
                        name = "Issuer wallet - ETC",
                        publicKey = "0f531508888a7bbf2c6a14df441a14401d8192df3f7c93b05d08115298565189",
                        secretKey = "4d14d1db55d4612ac2e0d2369e75120b1b46dde5b9a3b8ede4231f63e37d3ca30f531508888a7bbf2c6a14df441a14401d8192df3f7c93b05d08115298565189"
                    ),
                    MainWallet(
                        name = "Issuer wallet - ETC redeem",
                        publicKey = "441a504f50bca26a5b37e877c8101a593751438f8bca5f22f811aab39d30fe37",
                        secretKey = "f031816f8ee8f4c89e31dd66046ab22111e0bf23b172969759d62bd5aac6544d441a504f50bca26a5b37e877c8101a593751438f8bca5f22f811aab39d30fe37"
                    )
                )
            )
            PROD -> UserWithMultipleMainWallet2FA(
                "",
                "",
                listOf()
            )
            SHARED -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+for_accept_etc_tokens_2fa@gmail.com",
                "",
                listOf(
                    MainWallet(
                        name = "",
                        publicKey = "",
                        secretKey = "d8937be1d83f73abb68beae10d99bccb481704a9a075932e162c00039fd8fd9808856efbbbc44c4b5476596da89cf500266c789ce054a221a1eaa7fa41463745"
                    )
                )
            )
            TOKEN_TRUST -> UserWithMultipleMainWallet2FA(
                "",
                "",
                listOf()
            )
        }

        //ETC company исключительно для работы с ЕТС токенами не является ISSUER
        val ATM_USER_FOR_ETC_TOKENS = when (Environment.stand) {
            DEVELOP -> UserWithMultipleMainWallet2FA(
                "",
                "",
                listOf()
            )
            RELEASE -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+etc_tokens@gmail.com",
                "",
                listOf(
                    MainWallet(
                        name = "Main 1",
                        publicKey = "b964efabca34b3a57770851d8fbb2450b5c04182d63a1d052940d1cdee5a9591",
                        secretKey = "9c4dd244ad3106ab004e47bf2dd92060e3491103b76f6fbccd8120d3d4764dc2b964efabca34b3a57770851d8fbb2450b5c04182d63a1d052940d1cdee5a9591",
                        walletId = "zU2eG6acTyZ5UfMLP6HrrAWLakgNYTHyQdQkV9osG4s5Y5tDe"
                    )
                )
            )
            PREPROD -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+etc_tokens@gmail.com",
                "",
                listOf(
                    MainWallet(
                        name = "Main 1",
                        publicKey = "11ccd61d25f49ddca03b4b8e17eecf4a259575f5649ab4697d39956b15dc7612",
                        secretKey = "3c0784527ce0473d22582a3d368c3b8a0f70486b86077b01ced41786da4b640d11ccd61d25f49ddca03b4b8e17eecf4a259575f5649ab4697d39956b15dc7612",
                        walletId = "ApjJgGD97Mu7ibTnG3oQjA1wW6AqoFcnezoK5VDBrBw86Vm7D"
                    )
                )
            )
            PROD -> UserWithMultipleMainWallet2FA(
                "",
                "",
                listOf()
            )
            SHARED -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+for_accept_etc_tokens_2fa@gmail.com",
                "",
                listOf(
                    MainWallet(
                        name = "",
                        publicKey = "",
                        secretKey = "d8937be1d83f73abb68beae10d99bccb481704a9a075932e162c00039fd8fd9808856efbbbc44c4b5476596da89cf500266c789ce054a221a1eaa7fa41463745"
                    )
                )
            )
            TOKEN_TRUST -> UserWithMultipleMainWallet2FA(
                "",
                "",
                listOf()
            )
        }

        //ETC company исключительно для работы с ЕТС токенами не является ISSUER
        val ATM_USER_FOR_ETC_TOKENS_ONE = when (Environment.stand) {
            DEVELOP -> UserWithMultipleMainWallet2FA(
                "",
                "",
                listOf()
            )
            RELEASE -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+etc_tokens_one@gmail.com",
                "CU6673YZZJGWLBQB",
                listOf(
                    MainWallet(
                        name = "Main 1",
                        publicKey = "14bef5a75fec908c2537280b4d66f1ef8a5b98885a33b7539c22c1e49b41f28b",
                        secretKey = "00f0bb7709d57515fe3a6eb34570df409e112d4a83a01d4941000994f0b810dc14bef5a75fec908c2537280b4d66f1ef8a5b98885a33b7539c22c1e49b41f28b",
                        walletId = "GBy6gDTRPuKV24GMR19R5UcUiWGSeRWoVPaykz8SfrQv6uXmK"
                    )
                )
            )
            PREPROD -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+etc_tokens_one@gmail.com",
                "",
                listOf(
                    MainWallet(
                        name = "Main 1",
                        publicKey = "ef35837bb39560c5d8c8d3e29b43fad23a9ce10530e500ad6a32e9d6698d380f",
                        secretKey = "310ee865cbfd9e49b39a4d21fd16a651415b17995dbd34807f637dcc5ec1f01cef35837bb39560c5d8c8d3e29b43fad23a9ce10530e500ad6a32e9d6698d380f",
                        walletId = "2WEzpchSguhKH87gtK4tzVqxkb346252LnkrS6gtL3g8HxxeTi"
                    )
                )
            )
            PROD -> UserWithMultipleMainWallet2FA(
                "",
                "",
                listOf()
            )
            SHARED -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+for_accept_etc_tokens_2fa@gmail.com",
                "",
                listOf(
                )
            )
            TOKEN_TRUST -> UserWithMultipleMainWallet2FA(
                "",
                "",
                listOf()
            )
        }

        //ETC company исключительно для работы с ЕТС токенами не является ISSUER
        val ATM_USER_FOR_ETC_TOKENS_SECOND = when (Environment.stand) {
            DEVELOP -> UserWithMultipleMainWallet2FA(
                "",
                "",
                listOf()
            )
            RELEASE -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+etc_tokens_second@gmail.com",
                "",
                listOf(
                    MainWallet(
                        name = "Main 1",
                        publicKey = "88caae2cfffb86c6a9a258e770b497b03691bca983eb29ce4c3b337a5f902c1e",
                        secretKey = "e81de9b8803f790b1cb3d12d39b21411a0c1345d2f3b985b09e1ce8a2bf6a40f88caae2cfffb86c6a9a258e770b497b03691bca983eb29ce4c3b337a5f902c1e",
                        walletId = "hLB7oTTiohm5FCk65bTBALDe7Qbx4s4TvNNiiyHksCrkn9KuH"
                    )
                )
            )
            PREPROD -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+etc_tokens_second@gmail.com",
                "",
                listOf(
                    MainWallet(
                        name = "Main 1",
                        publicKey = "15c134ae5069fa929c3bb3483732c78b407445b34da185597167426e497bef1a",
                        secretKey = "525f08cabfda8c3f6c9b93d4816eb01729d5f982fba4af61549b102672e30ad415c134ae5069fa929c3bb3483732c78b407445b34da185597167426e497bef1a",
                        walletId = "2bLqEgjgTsxjbJ9Awjg9KAowNcLE8uNtc9yGMYB67aXHXD14PQ"
                    )
                )
            )
            PROD -> UserWithMultipleMainWallet2FA(
                "",
                "",
                listOf()
            )
            SHARED -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+for_accept_etc_tokens_2fa@gmail.com",
                "",
                listOf(
                )
            )
            TOKEN_TRUST -> UserWithMultipleMainWallet2FA(
                "",
                "",
                listOf()
            )
        }

        //ETC company исключительно для работы с ЕТС токенами не является ISSUER
        val ATM_USER_FOR_ETC_TOKENS_THIRD = when (Environment.stand) {
            DEVELOP -> UserWithMultipleMainWallet2FA(
                "",
                "",
                listOf()
            )
            RELEASE -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+etc_tokens_third@gmail.com",
                "",
                listOf(
                    MainWallet(
                        name = "Main 1",
                        publicKey = "a0cc692a59834e5c65573c9bf3122697dde7c553c879401ed6a871b758034afc",
                        secretKey = "1a5c4a414597763990604288bfdfb6f58c4ee3376cc98872ee84bfe23446e95fa0cc692a59834e5c65573c9bf3122697dde7c553c879401ed6a871b758034afc",
                        walletId = "2LvSXNbnae37FwysCcoj5tTJ5JiKFodnohXHu4opdbxQrjwLDa"
                    )
                )
            )
            PREPROD -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+etc_tokens_third@gmail.com",
                "",
                listOf(
                    MainWallet(
                        name = "Main 1",
                        publicKey = "743b81df5a73da3528f68bf433ab345fcffcbc1fdc3ed4f0bcebf264dd6414a8",
                        secretKey = "bd7ee1c894d8dda1c12cf5eabb2d55ddc038f887c5374e80aef99617a130794f743b81df5a73da3528f68bf433ab345fcffcbc1fdc3ed4f0bcebf264dd6414a8",
                        walletId = "zXVzdRPJReRvLazguBPrfEgUhgY18PbK56U41iemFf58R4jY6"
                    )
                )
            )
            PROD -> UserWithMultipleMainWallet2FA(
                "",
                "",
                listOf()
            )
            SHARED -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+for_accept_etc_tokens_2fa@gmail.com",
                "",
                listOf(
                )
            )
            TOKEN_TRUST -> UserWithMultipleMainWallet2FA(
                "",
                "",
                listOf()
            )
        }

        // Atm user, inviteToCompany, dumbCompany
        val ATM_USER_INVITE_TO_COMPANY = when (Environment.stand) {
            DEVELOP -> DefaultUser()
            RELEASE -> DefaultUser("aft.uat.sdex+invite_tocompany@gmail.com")
            PREPROD -> DefaultUser("aft.uat.sdex+invite_to_company@gmail.com")
            PROD -> DefaultUser()
            SHARED -> DefaultUser("aft.uat.sdex+invite_to_company@gmail.com")
            TOKEN_TRUST -> DefaultUser("aft.uat.sdex+invite_to_company@gmail.com")
        }

        // Atm user, inviteToCompany
        val ATM_USER_DUMB_INVATED_TO_COMPANY = when (Environment.stand) {
            DEVELOP -> DefaultUser()
            RELEASE -> DefaultUser("aft.uat.sdex+dumb_invited_to_company@gmail.com")
            PREPROD -> DefaultUser("aft.uat.sdex+dumb_invited_to_company@gmail.com")
            PROD -> DefaultUser()
            SHARED -> DefaultUser("aft.uat.sdex+dumb_invited_to_company@gmail.com")
            TOKEN_TRUST -> DefaultUser("aft.uat.sdex+dumb_invited_to_company@gmail.com")
        }

        // OTF_OPERATION
        val ATM_USER_2FA_OTF_OPERATION = when (Environment.stand) {
            DEVELOP -> UserWithMainWalletAndOtf2FA(
                email = "aft.uat.sdex+atm_otf_operation@gmail.com",
                oAuthSecret = "AF4CKWBPE3UZG7MS",
                otfWallet = OtfWallet(
                    publicKey = "02ec0e85f07157ab36259e8d7149797fe24fc8eeb76151c41a98c4f6a55bc9c9",
                    secretKey = "936556a88877e407e31b89bae1c6cf792f18a2af00fc22eb3c89bf014a35db4f02ec0e85f07157ab36259e8d7149797fe24fc8eeb76151c41a98c4f6a55bc9c9"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "609f2d05d0c60fccb65e9ada310effb9a57065984893b8f71c8a221be869065f",
                    secretKey = "a4f532104d044fc85dc692a8be49163ad9a9d25bedeacc57712d82f90fe58562609f2d05d0c60fccb65e9ada310effb9a57065984893b8f71c8a221be869065f"
                )
            )

            RELEASE -> UserWithMainWalletAndOtf2FA(
                email = "aft.uat.sdex+atm_otf_operation@gmail.com",
                oAuthSecret = "P3QC7MVA2HM2VKSC",
                otfWallet = OtfWallet(
                    publicKey = "2657cf4cd08c4487a7d0456d657339f817db0742855f2f00dca2e8e8314eafa7",
                    secretKey = "b2f7c9ebc7a4e10651acc6e647924223867dad33f62563c0500083833a1681592657cf4cd08c4487a7d0456d657339f817db0742855f2f00dca2e8e8314eafa7"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "e877b0cb0c60effd7131af2ae323c8cbed3accd5de1305ac84f2178e3e30003b",
                    secretKey = "df5890909324eb6ad1dd23412ce100f8c6000950ab439f1c8bad8eb8b0258335e877b0cb0c60effd7131af2ae323c8cbed3accd5de1305ac84f2178e3e30003b"
                )
            )

            PREPROD -> UserWithMainWalletAndOtf2FA(
                email = "aft.uat.sdex+atm_otf_operation@gmail.com",
                oAuthSecret = "VXCIGLGHFBRNZECJ",
                otfWallet = OtfWallet(
                    publicKey = "5ea5fba1cacffdd6e6e1f38380292b7232396d2e44d93d9b87484fd94e8bc97c",
                    secretKey = "3f4b0f994a689b31c4622e5e66bd4bf4e2309de51deee7ae984a0efe90caec805ea5fba1cacffdd6e6e1f38380292b7232396d2e44d93d9b87484fd94e8bc97c"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "3da47d47eef103e5ec6e99e05d223e7b52bf4dc7dd2424908b8668c237237f33",
                    secretKey = "42d73a9931e6228214cb48fa151ee7f9c1b79e090b0c72371470a27be0601cf13da47d47eef103e5ec6e99e05d223e7b52bf4dc7dd2424908b8668c237237f33"
                )
            )

            PROD -> UserWithMainWalletAndOtf2FA(
                oAuthSecret = "",
                otfWallet = OtfWallet(),
                mainWallet = MainWallet()
            )

            SHARED -> UserWithMainWalletAndOtf2FA(
                email = "aft.uat.sdex+atm_otf_operation@gmail.com",
                oAuthSecret = "UOGR7TE7SMJZ7WT2",
                otfWallet = OtfWallet(
                    publicKey = "c928380628b9fcac62cf14a59388fc9b8a3cc1ace729055bb887cd37c07d1c4f",
                    secretKey = "ce555e96bdda149e28f36d6f6546d3d563a081fccfb72ea79eba029362f82017c928380628b9fcac62cf14a59388fc9b8a3cc1ace729055bb887cd37c07d1c4f"
                ),
                mainWallet = MainWallet(
                    publicKey = "e70cc77519fae9b74cbe142d6bba398dc7db248d550275d1425359a084374492",
                    secretKey = "91ad8da4ecae1c4b4eae60fda8dea34eab778cfd69b32c52000b8bb6ed4decc5e70cc77519fae9b74cbe142d6bba398dc7db248d550275d1425359a084374492"
                )
            )
            TOKEN_TRUST -> UserWithMainWalletAndOtf2FA(
                "aft.uat.sdex+atm_otf_operation@gmail.com",
                "N37N3K5Y25DBEKBO",
                otfWallet = OtfWallet(
                    publicKey = "e60c84eef4264d7196f206bcc521611165d351bc070bd653abcb603d7a8ac1da",
                    secretKey = "e3230da463bed0977145f7f1e70345bec05c64895ddc8eaf2d984248250e92f5e60c84eef4264d7196f206bcc521611165d351bc070bd653abcb603d7a8ac1da"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "7167a0b3ca3c4390d6c9e376a212062cc5d4ade52200aba1fa814373b2364065",
                    secretKey = "8e95bd57dfd5c0c67f192a1b82aeaed47f090bf50aa5a900d89cca604a20e0397167a0b3ca3c4390d6c9e376a212062cc5d4ade52200aba1fa814373b2364065"
                )
            )
        }

        // OTF_OPERATION_WITHOUT2FA
        val ATM_USER_2FA_OTF_OPERATION_WITHOUT2FA = when (Environment.stand) {
            DEVELOP -> UserWithMainWalletAndOtf(
                email = "aft.uat.sdex+atm_otf_operation_without_oauth@gmail.com",
                otfWallet = OtfWallet(
                    publicKey = "0a6289b98ceebed2b1f0b5920ced1d3980a93bf9b20c7564ee79ec0c2b8b5a87",
                    secretKey = "e8b50e3391e4d6a8846f45a0e3809881fa69f11a687e332bfb76684f75f1b4630a6289b98ceebed2b1f0b5920ced1d3980a93bf9b20c7564ee79ec0c2b8b5a87"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "f467dfc0708cc71b80db3b49f47c42373d63bb6c2727b1feddbf6620740b1ce0",
                    secretKey = "c01d1b66add336b590239256c402d8422a168363ff20536082afe95790878a0af467dfc0708cc71b80db3b49f47c42373d63bb6c2727b1feddbf6620740b1ce0"
                )
            )

            RELEASE -> UserWithMainWalletAndOtf(
                email = "aft.uat.sdex+atm_otf_operation_without_oauth@gmail.com",
                otfWallet = OtfWallet(
                    publicKey = "8b80f2276e8bae7667190ed73d7af5a81a7bc062a6ccc9cf3a7746ebfaca21ed",
                    secretKey = "edf43a06c51a15a4c0d8627eba0c63e04a048132b013ee1f30fe83b0f3cd175f8b80f2276e8bae7667190ed73d7af5a81a7bc062a6ccc9cf3a7746ebfaca21ed"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "431be5407e42a40f3809c6586164e6e4f619228968431323b61aad689906aaa9",
                    secretKey = "159637b61fbeda5afef55f8da50aac60d0308774b786191af850d48d1a4f537c431be5407e42a40f3809c6586164e6e4f619228968431323b61aad689906aaa9"
                )
            )

            PREPROD -> UserWithMainWalletAndOtf(
                email = "aft.uat.sdex+atm_otf_operation_without_oauth@gmail.com",
                otfWallet = OtfWallet(
                    publicKey = "e6389004a03df1c524739ee8d1b2212e0d4d9bf48fc27e402016631e9b9e0919",
                    secretKey = "57ef1c11e0f6ff1cc3e5935c6c68ca1157e1302c90693158a5d018a5ca3b6a82e6389004a03df1c524739ee8d1b2212e0d4d9bf48fc27e402016631e9b9e0919"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "5dd14d55cf4b99b91d306666140b115561034be65230b8add64137b039564f3c",
                    secretKey = "886032880ef702177ca37343fbeae0c055c8b34cef0a42569841e51a517527e35dd14d55cf4b99b91d306666140b115561034be65230b8add64137b039564f3c"
                )
            )

            PROD -> UserWithMainWalletAndOtf(
                otfWallet = OtfWallet(),
                mainWallet = MainWallet()
            )

            SHARED -> UserWithMainWalletAndOtf(
                email = "aft.uat.sdex+atm_otf_operation_without_oauth@gmail.com",
                otfWallet = OtfWallet(
                    publicKey = "634421d1b94088a7937bf5a2159d6bab54bab85c881f8f1b0ceff9bcb73a5657",
                    secretKey = "bf3b4a48a10fa7b84bb0bc1c95819380e231185811e4e3bb83b58852e50b9872634421d1b94088a7937bf5a2159d6bab54bab85c881f8f1b0ceff9bcb73a5657"
                ),
                mainWallet = MainWallet(
                    publicKey = "777c0e03b02161582f89a6f83e1a7f2809ec6b567bc1e56bf298386f000ae9e1",
                    secretKey = "297e8ad0401e61ba0f5baa3ba634bd3b61bf3bef033630fda1e27c5f876a7b4f777c0e03b02161582f89a6f83e1a7f2809ec6b567bc1e56bf298386f000ae9e1"
                )
            )
            TOKEN_TRUST -> UserWithMainWalletAndOtf(
                "aft.uat.sdex+atm_otf_operation_without_oauth@gmail.com",
                otfWallet = OtfWallet(
                    publicKey = "ca6fd29ed973e4b59facc27b9f906b43827f5c9b87e3fcb56c0f618835bee466",
                    secretKey = "affe239567d358ef51128b639402395ee123296c52c6caf153b26c051fb7872bca6fd29ed973e4b59facc27b9f906b43827f5c9b87e3fcb56c0f618835bee466"
                ),
                mainWallet = MainWallet(
                    name = "Main 3",
                    publicKey = "bebaea23e9273cbbdb583d0e5a8f758d9925297bafbb2d4654cf5f2e2dd8091f",
                    secretKey = "a1d88036941248dbd8d04ea7fb7ce18f08b938e77f2b709cfb60510918061860bebaea23e9273cbbdb583d0e5a8f758d9925297bafbb2d4654cf5f2e2dd8091f"
                )
            )
        }

        // OTF_OPERATION_2
        val ATM_USER_2FA_OTF_OPERATION_SECOND = when (Environment.stand) {
            DEVELOP -> UserWithMainWalletAndOtf2FA(
                email = "aft.uat.sdex+atm_otf_operation_second@gmail.com",
                oAuthSecret = "344FXZVND64AZIAT",
                otfWallet = OtfWallet(
                    publicKey = "a61fff683c9c250827c22f502ce428d82849cd3cf195e10733d9652c9bd37064",
                    secretKey = "148b077d6173cb5650e3ece931417b3260b539c59b1f50005a7550d4169f7e02a61fff683c9c250827c22f502ce428d82849cd3cf195e10733d9652c9bd37064"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "fba4f4961b9bff960c13626295beca67e5d8caccdccb702d38a671891b680560",
                    secretKey = "3e3dae2d630f4f855249008eaaedddfc5e8ec392493e7b48c4ed89e73336fe09fba4f4961b9bff960c13626295beca67e5d8caccdccb702d38a671891b680560"
                )
            )

            RELEASE -> UserWithMainWalletAndOtf2FA(
                email = "aft.uat.sdex+atm_otf_operation_second@gmail.com",
                oAuthSecret = "EFZ6K7HXHOPVP465",
                otfWallet = OtfWallet(
                    publicKey = "41cfb675ad299841aebd3023175d4a4292180f03c020ff4d93a71792c0917329",
                    secretKey = "173bc431b0a76462ccae28eacc6716a32ac96fa50b3d7edbe1d5c94ccb6bb38541cfb675ad299841aebd3023175d4a4292180f03c020ff4d93a71792c0917329"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "0f7ae0eee56a8fb4f4b021509e36129b03da6bfd6978e9bc2628a0f96111a523",
                    secretKey = "4ab8d3b30f6b9f6b2b7ac617e740c7cfd89ec104444f3cad1760f62e8d9c46ad0f7ae0eee56a8fb4f4b021509e36129b03da6bfd6978e9bc2628a0f96111a523"
                )
            )

            PREPROD -> UserWithMainWalletAndOtf2FA(
                email = "aft.uat.sdex+atm_otf_operation_second@gmail.com",
                oAuthSecret = "UUGZF4KC5KNTDG5V",
                otfWallet = OtfWallet(
                    publicKey = "11935dc66c41b7539c85e49681687e1606725e8cb394101043e695d17a5599ab",
                    secretKey = "be2f4b1280392b99259580d7a57b944872ce0cc8bc200b2de2dd425ed2e56bea11935dc66c41b7539c85e49681687e1606725e8cb394101043e695d17a5599ab"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "857fe272dbd8ff45e5393eebd6965ecb7921312a466e18cffbd93b11118062e6",
                    secretKey = "651a1e867738d2ad6f9d63d0ddc65e71caa95dad7c7f66e9bca00345a7c6d738857fe272dbd8ff45e5393eebd6965ecb7921312a466e18cffbd93b11118062e6"
                )
            )

            PROD -> UserWithMainWalletAndOtf2FA(
                oAuthSecret = "",
                otfWallet = OtfWallet(),
                mainWallet = MainWallet()
            )

            SHARED -> UserWithMainWalletAndOtf2FA(
                email = "aft.uat.sdex+atm_otf_operation_second@gmail.com",
                oAuthSecret = "75DK5IQAEIDSMKNM",
                otfWallet = OtfWallet(
                    publicKey = "43c2afe1f679d6a9b80d3712ccd5aa2e9db36297d832fc647885d15e15f914d8",
                    secretKey = "e8d7f3ef873ba6bd911b1650a19836b5f871ae21278711993e45db7ff3c8fc0343c2afe1f679d6a9b80d3712ccd5aa2e9db36297d832fc647885d15e15f914d8"
                ),
                mainWallet = MainWallet(
                    publicKey = "43c2afe1f679d6a9b80d3712ccd5aa2e9db36297d832fc647885d15e15f914d8",
                    secretKey = "e8d7f3ef873ba6bd911b1650a19836b5f871ae21278711993e45db7ff3c8fc0343c2afe1f679d6a9b80d3712ccd5aa2e9db36297d832fc647885d15e15f914d8"
                )
            )
            TOKEN_TRUST -> UserWithMainWalletAndOtf2FA(
                "aft.uat.sdex+atm_otf_operation_second@gmail.com",
                "Q3SVM73HHTBMDLSX",
                otfWallet = OtfWallet(
                    publicKey = "f93595ac0791ef4746754eba1a4c5a44d10bb566ac9ae2e9861e7687aaf250e8",
                    secretKey = "4d5711722f2f6b128ef9f60dad72726b2ba48f1703af2d57d8fe7a6ef15ebc1df93595ac0791ef4746754eba1a4c5a44d10bb566ac9ae2e9861e7687aaf250e8"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "2ddb52c8149e6478387084835a8e89f3074b307ceadec2a999e46a31a343ee90",
                    secretKey = "e9d6593a65a839672266146557cb4247c92697747e43cca20d054fc256e37e4d2ddb52c8149e6478387084835a8e89f3074b307ceadec2a999e46a31a343ee90"
                )
            )
        }

        val ATM_USER_2FA_OTF_OPERATION_THIRD = when (Environment.stand) {
            DEVELOP -> UserWithMainWalletAndOtf2FA(
                email = "aft.uat.sdex+atm_otf_operation_third@gmail.com",
                oAuthSecret = "ZVA36INTPBU67RGJ",
                otfWallet = OtfWallet(
                    publicKey = "9e518cc0b3dad86fd9c9772f16e58c8a97e4cd1cd17559953477b1ba87b154f4",
                    secretKey = "21777ec39e63db53d465eb402b338217f298c016e7eb35fa98e9a4b84812e3f49e518cc0b3dad86fd9c9772f16e58c8a97e4cd1cd17559953477b1ba87b154f4"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "4e4a51474273f0ad7c059c6262f25f83bac7a2a4415aa849584c715173c5aea6",
                    secretKey = "1bfc9aa94839689e074c594fd1127009f169c61b29f9d967e74b87acde9365c64e4a51474273f0ad7c059c6262f25f83bac7a2a4415aa849584c715173c5aea6"
                )
            )

            RELEASE -> UserWithMainWalletAndOtf2FA(
                email = "aft.uat.sdex+atm_otf_operation_third@gmail.com",
                oAuthSecret = "MI7YEKFVIBJAADYA",
                otfWallet = OtfWallet(
                    publicKey = "19309d9dc1280c84f5ad4751da53536abc97d845d0ee7afd7a40bdbd5dd2acf9",
                    secretKey = "8164172334db30da79d7b3f67fbe0dac5492fa9b813a100b17687cde218c42b619309d9dc1280c84f5ad4751da53536abc97d845d0ee7afd7a40bdbd5dd2acf9"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "165fb8fa335599e4414b6a2fb27693d0bcfa8e711b3e347fcebc44aa4cf1cd5b",
                    secretKey = "f0a0114367da04fc2b6a437ddf405d7caebdffd0413e5d7c76ae3a74678e0d57165fb8fa335599e4414b6a2fb27693d0bcfa8e711b3e347fcebc44aa4cf1cd5b"
                )
            )

            PREPROD -> UserWithMainWalletAndOtf2FA(
                email = "aft.uat.sdex+atm_otf_operation_third@gmail.com",
                oAuthSecret = "KB6DNCWZEG6YR3XV",
                otfWallet = OtfWallet(
                    publicKey = "4dd1fc08e83513921842500d812090b43690c12103ae90740cb0b7b4d979f07f",
                    secretKey = "9ca7e9b5deb7dcd71d3f5a5c600c75f9c913824a8c8b14147e332c1c27621e154dd1fc08e83513921842500d812090b43690c12103ae90740cb0b7b4d979f07f"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "7fbdf8db8b1350573981290d63727fd9b548f8f5cabfabb5893a72155469fd2c",
                    secretKey = "f1ee84ea5b1164701df3e2402cda0f54ef06fafe503b37f2aae72a3e26af90547fbdf8db8b1350573981290d63727fd9b548f8f5cabfabb5893a72155469fd2c"
                )
            )

            PROD -> UserWithMainWalletAndOtf2FA(
                oAuthSecret = "",
                otfWallet = OtfWallet(),
                mainWallet = MainWallet()
            )

            SHARED -> UserWithMainWalletAndOtf2FA(
                email = "aft.uat.sdex+@gmail.com",
                oAuthSecret = "",
                otfWallet = OtfWallet(
                    publicKey = "",
                    secretKey = ""
                ),
                mainWallet = MainWallet(
                    publicKey = "",
                    secretKey = ""
                )
            )
            TOKEN_TRUST -> UserWithMainWalletAndOtf2FA(
                "",
                "",
                otfWallet = OtfWallet(
                    publicKey = "",
                    secretKey = ""
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "",
                    secretKey = ""
                )
            )
        }

        val ATM_USER_2FA_OTF_OPERATION_FORTH = when (Environment.stand) {
            DEVELOP -> UserWithMainWalletAndOtf2FA(
                email = "aft.uat.sdex+atm_otf_operation_forth@gmail.com",
                oAuthSecret = "JC6CGBYHEWIFJO5N",
                otfWallet = OtfWallet(
                    publicKey = "6ba907d4baab0d5212eb189e63b8c17a7cf5f74ac63cb8e26cd2440e8a8411a7",
                    secretKey = "674e573d141e1f84ec27cd975fcc4dea378c05e4e73427ee2c1bd6cee10cd7c26ba907d4baab0d5212eb189e63b8c17a7cf5f74ac63cb8e26cd2440e8a8411a7"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "03ec79db4e25f67b5d44afe04f2171af6228d68601242cd726f976b44bf9d228",
                    secretKey = "cd024063ab582744625752c23b8feca425ed199b8130fe1f1bce56535a05078803ec79db4e25f67b5d44afe04f2171af6228d68601242cd726f976b44bf9d228"
                )
            )

            RELEASE -> UserWithMainWalletAndOtf2FA(
                email = "aft.uat.sdex+atm_otf_operation_forth@gmail.com",
                oAuthSecret = "QDDQGA5RGSWIWBNN",
                otfWallet = OtfWallet(
                    publicKey = "a5ff49394d00adf15e43b57ce5ef2dc7abf6147b70a38347f9cf92092b57cd71",
                    secretKey = "dc55896cf70710e0edc5a5e03219834bc471f6dda499605ab05a046f300608b8a5ff49394d00adf15e43b57ce5ef2dc7abf6147b70a38347f9cf92092b57cd71"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "3c1ef97e426f7fd71a2aef2d8856706a0fcff3decba0441817ada6977c5be1ff",
                    secretKey = "48479a61bd8251d140635197345fb38c67b158137b8841eb798c6062014a78833c1ef97e426f7fd71a2aef2d8856706a0fcff3decba0441817ada6977c5be1ff"
                )
            )


            PREPROD -> UserWithMainWalletAndOtf2FA(
                email = "aft.uat.sdex+atm_otf_operation_forth@gmail.com",
                oAuthSecret = "3FUEPW5GFNGQ65WC",
                otfWallet = OtfWallet(
                    publicKey = "c4699e38615703704fb52728d783e4d8666b5e0b1369f5e05438fea3430afb4a",
                    secretKey = "f2f9b33df156b73fb8999af1e793e2b842eab9bbdb2faaceb2b9796be78c5cc0c4699e38615703704fb52728d783e4d8666b5e0b1369f5e05438fea3430afb4a"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "c88ba47461af0b26ac0d7655f165c4e507bb5e0d56c20aca6e4723a822b1b9be",
                    secretKey = "e83895586491723ab36a14be1b93d061349209971663a48a408baf2ff27bc6c3c88ba47461af0b26ac0d7655f165c4e507bb5e0d56c20aca6e4723a822b1b9be"
                )
            )

            PROD -> UserWithMainWalletAndOtf2FA(
                oAuthSecret = "",
                otfWallet = OtfWallet(),
                mainWallet = MainWallet()
            )

            SHARED -> UserWithMainWalletAndOtf2FA(
                email = "aft.uat.sdex+@gmail.com",
                oAuthSecret = "",
                otfWallet = OtfWallet(
                    publicKey = "",
                    secretKey = ""
                ),
                mainWallet = MainWallet(
                    publicKey = "",
                    secretKey = ""
                )
            )
            TOKEN_TRUST -> UserWithMainWalletAndOtf2FA(
                "",
                "",
                otfWallet = OtfWallet(
                    publicKey = "",
                    secretKey = ""
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "",
                    secretKey = ""
                )
            )
        }

        val ATM_USER_2FA_OTF_OPERATION_FIFTH = when (Environment.stand) {
            DEVELOP -> UserWithMainWalletAndOtf2FA(
                email = "aft.uat.sdex+atm_otf_operation_fifth@gmail.com",
                oAuthSecret = "",
                otfWallet = OtfWallet(
                    publicKey = "3ec0b030f9b30a3fffebe7680e3ca57fd38eb248d8fb9c5b99b226b7ee6a63f4",
                    secretKey = "18b7b86b2734f8473e86515f55226d6989ebb0f71381d78132b12827e38eb3a53ec0b030f9b30a3fffebe7680e3ca57fd38eb248d8fb9c5b99b226b7ee6a63f4"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "6a1df8bbd1694e58d4be3beb096dc47ac2d346194f05910aba3f5cb51b0b8f41",
                    secretKey = "fa8d8836e2c6b3103450cc8c3ce01845b5181c8b2329e72310e4093ce1388ea16a1df8bbd1694e58d4be3beb096dc47ac2d346194f05910aba3f5cb51b0b8f41"
                )
            )

            RELEASE -> UserWithMainWalletAndOtf2FA(
                email = "aft.uat.sdex+atm_otf_operation_fifth@gmail.com",
                oAuthSecret = "",
                otfWallet = OtfWallet(
                    publicKey = "b848c928cf71e7b22dd4117a49cdc34b4f53f8e5c4ab4b32f8842b821c0c9d85",
                    secretKey = "e1b40e206f55b5f6a55fdf1f21a74f28a6a0746b43e97e3d160d590c849da3d3b848c928cf71e7b22dd4117a49cdc34b4f53f8e5c4ab4b32f8842b821c0c9d85"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "35159c6d2219dd77b16af940013f0e2402f355961eebd61eb56329cd2b4af279",
                    secretKey = "608e0ad490990667a57b6037ca1b41e016583debd22ff34f63f87231233e462b35159c6d2219dd77b16af940013f0e2402f355961eebd61eb56329cd2b4af279"
                )
            )

            PREPROD -> UserWithMainWalletAndOtf2FA(
                email = "aft.uat.sdex+atm_otf_operation_fifth@gmail.com",
                oAuthSecret = "",
                otfWallet = OtfWallet(
                    publicKey = "f3cb38559d540fddd93af6319a22d264cdd19e86047109b2676ab7b9a2a29432",
                    secretKey = "d3296859532f9d670e823fea114165a32329eec3177d2ba8c2068ad76bc0639af3cb38559d540fddd93af6319a22d264cdd19e86047109b2676ab7b9a2a29432"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "70ae5176d6d8e90d81c3cca032010a414fd6329ac44732d9719587d467832785",
                    secretKey = "cde285e12377e579ac373d5c3a4c0034e4cfdd02fd70249527d6f37b7ec5ab8d70ae5176d6d8e90d81c3cca032010a414fd6329ac44732d9719587d467832785"
                )
            )

            PROD -> UserWithMainWalletAndOtf2FA(
                oAuthSecret = "",
                otfWallet = OtfWallet(),
                mainWallet = MainWallet()
            )

            SHARED -> UserWithMainWalletAndOtf2FA(
                email = "aft.uat.sdex+@gmail.com",
                oAuthSecret = "",
                otfWallet = OtfWallet(
                    publicKey = "",
                    secretKey = ""
                ),
                mainWallet = MainWallet(
                    publicKey = "",
                    secretKey = ""
                )
            )
            TOKEN_TRUST -> UserWithMainWalletAndOtf2FA(
                "",
                "",
                otfWallet = OtfWallet(
                    publicKey = "",
                    secretKey = ""
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "",
                    secretKey = ""
                )
            )
        }

        val ATM_USER_2FA_OTF_OPERATION_SIXTH = when (Environment.stand) {
            DEVELOP -> UserWithMainWalletAndOtf2FA(
                email = "aft.uat.sdex+atm_otf_operation_sixth@gmail.com",
                oAuthSecret = "",
                otfWallet = OtfWallet(
                    publicKey = "47950518c9a9f1a24d1d590ac22b2fc7bfb04a3a5577f38bc84fd7f10b7d278a",
                    secretKey = "de8accd4cf796dda34ebd23c71b33886614d11cbbb0ac49eeb4a9489d9ad31f647950518c9a9f1a24d1d590ac22b2fc7bfb04a3a5577f38bc84fd7f10b7d278a"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "4c6cfee7f37542b2d4b12990025d1a62509092f91d9c8103e3e846ad8c150758",
                    secretKey = "5f2736946127dfe41ff8351f380af578ed5c22fa9bbfca148783c7c6ef78b0e34c6cfee7f37542b2d4b12990025d1a62509092f91d9c8103e3e846ad8c150758"
                )
            )

            RELEASE -> UserWithMainWalletAndOtf2FA(
                email = "aft.uat.sdex+atm_otf_operation_sixth@gmail.com",
                oAuthSecret = "",
                otfWallet = OtfWallet(
                    publicKey = "26568c6f46ec0aa467ee01a0446eab520efb0fc9b00e5f0e161b0e418b10c480",
                    secretKey = "d461d841fd0510edcb3aac63a86fe175a8967b989302b7708adc66aba17868db26568c6f46ec0aa467ee01a0446eab520efb0fc9b00e5f0e161b0e418b10c480"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "1b4dee58d562dae3d371dacc15e21402fb74d5076140b1c82eb41cab65d0f075",
                    secretKey = "d765b3d1e70af50d1f6eb44dd3c2d88e5db1fcc54b60c29c210b0701e2e453d81b4dee58d562dae3d371dacc15e21402fb74d5076140b1c82eb41cab65d0f075"
                )
            )


            PREPROD -> UserWithMainWalletAndOtf2FA(
                email = "aft.uat.sdex+atm_otf_operation_sixth@gmail.com",
                oAuthSecret = "",
                otfWallet = OtfWallet(
                    publicKey = "a85c697ff8ba3c0484fd2b3d546c0db7cfefba4c783f6d16457b9345505ef392",
                    secretKey = "46983eebb7aa56473a4c5cef583b447916592015651e77e5610b715af58710d2a85c697ff8ba3c0484fd2b3d546c0db7cfefba4c783f6d16457b9345505ef392"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "9ba9689197ab6e384b85abaa397a62f8bc34ed645dcba5aeaa085f28250d4db3",
                    secretKey = "a9cef1ac97675c73cbca056ca7a1def35cdfbd43c8e87efac881b08c29ae9eed9ba9689197ab6e384b85abaa397a62f8bc34ed645dcba5aeaa085f28250d4db3"
                )
            )

            PROD -> UserWithMainWalletAndOtf2FA(
                oAuthSecret = "",
                otfWallet = OtfWallet(),
                mainWallet = MainWallet()
            )

            SHARED -> UserWithMainWalletAndOtf2FA(
                email = "aft.uat.sdex+@gmail.com",
                oAuthSecret = "",
                otfWallet = OtfWallet(
                    publicKey = "",
                    secretKey = ""
                ),
                mainWallet = MainWallet(
                    publicKey = "",
                    secretKey = ""
                )
            )
            TOKEN_TRUST -> UserWithMainWalletAndOtf2FA(
                "",
                "",
                otfWallet = OtfWallet(
                    publicKey = "",
                    secretKey = ""
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "",
                    secretKey = ""
                )
            )
        }

        val ATM_USER_2FA_OTF_OPERATION_SEVENTH = when (Environment.stand) {
            DEVELOP -> UserWithMainWalletAndOtf2FA(
                email = "aft.uat.sdex+atm_otf_operation_seventh@gmail.com",
                oAuthSecret = "OXQ64PNPNLGKRERE",
                otfWallet = OtfWallet(
                    publicKey = "62d5e0854547d9c1b86e1804247dece97ced1727621f982651bd5a001a7c200a",
                    secretKey = "feb4ded37dcfd3a5331b5c00b3fb94e41fd3eabbf1ac0306eb152be4c74ff36462d5e0854547d9c1b86e1804247dece97ced1727621f982651bd5a001a7c200a"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "63dc17275824b3f5eb02019fbfcdce566426349dc7a54d535a36a6f0d1ffbd0e",
                    secretKey = "7bdb1519dabd2113f6026f41b1cd576bfc321b902d9fe8fd3b82faed62ce861063dc17275824b3f5eb02019fbfcdce566426349dc7a54d535a36a6f0d1ffbd0e"
                )
            )

            RELEASE -> UserWithMainWalletAndOtf2FA(
                email = "aft.uat.sdex+atm_otf_operation_seventh@gmail.com",
                oAuthSecret = "J4WBRTXOGRZ3D66Q",
                otfWallet = OtfWallet(
                    publicKey = "71df720f94dce6c73858cd1b6db5bf5a78c61da4877957727672a2fa00476b53",
                    secretKey = "03f64a36a6dedbdfa9bf897d3cc8e4de062040163cb1140322d7f4dbeaf7b84171df720f94dce6c73858cd1b6db5bf5a78c61da4877957727672a2fa00476b53"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "a2b368c29644fa2ab145d8312f2c0cd94ca9e934daef6b0810f45266d79ee7c8",
                    secretKey = "a991b681eb51787e0324eaae33de8ff11da1f2d2fff5ab07b936d0ec6ca48dc7a2b368c29644fa2ab145d8312f2c0cd94ca9e934daef6b0810f45266d79ee7c8"
                )
            )

            PREPROD -> UserWithMainWalletAndOtf2FA(
                email = "aft.uat.sdex+atm_otf_operation_seventh@gmail.com",
                oAuthSecret = "UD5HMJ3N74IINQQU",
                otfWallet = OtfWallet(
                    publicKey = "121cd7220f250b4a13feaf8a98785bed7a5df954101abe15275c4c760065aad4",
                    secretKey = "3cd0a981a7e50589ae274a6be64398332c27ee1bd3c4d9529d733744983f3f24121cd7220f250b4a13feaf8a98785bed7a5df954101abe15275c4c760065aad4"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "b0cbb6218d8880fcd5392bce193859263552d8c98bb6b7cec5ad62a76a4e204f",
                    secretKey = "5615e72550ebfb174743200d43a6d5231b7efe6f446c08580cf35d322a0ba729b0cbb6218d8880fcd5392bce193859263552d8c98bb6b7cec5ad62a76a4e204f"
                )
            )

            PROD -> UserWithMainWalletAndOtf2FA(
                oAuthSecret = "",
                otfWallet = OtfWallet(),
                mainWallet = MainWallet()
            )

            SHARED -> UserWithMainWalletAndOtf2FA(
                email = "aft.uat.sdex+@gmail.com",
                oAuthSecret = "",
                otfWallet = OtfWallet(
                    publicKey = "",
                    secretKey = ""
                ),
                mainWallet = MainWallet(
                    publicKey = "",
                    secretKey = ""
                )
            )
            TOKEN_TRUST -> UserWithMainWalletAndOtf2FA(
                "",
                "",
                otfWallet = OtfWallet(
                    publicKey = "",
                    secretKey = ""
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "",
                    secretKey = ""
                )
            )
        }

        val ATM_USER_2FA_OTF_OPERATION_EIGHTH = when (Environment.stand) {
            DEVELOP -> UserWithMainWalletAndOtf2FA(
                email = "aft.uat.sdex+atm_otf_operation_eighth@gmail.com",
                oAuthSecret = "K6HEJWRQOMXGOA6S",
                otfWallet = OtfWallet(
                    publicKey = "f80c15cd42080f7be81222bd78eccf61dbc7228f96c2814e298380677cd857b0",
                    secretKey = "125c111ce302818f3087bce35c6f8a0ca48eda1df4e6e36eba148db529898feaf80c15cd42080f7be81222bd78eccf61dbc7228f96c2814e298380677cd857b0"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "7c40ff08a4cf6095a105fc944d4254501c7edb27d23bee5bf49689c6d5d09442",
                    secretKey = "1e55c3c65364c30518bf5ebdd128f8afb70d73c3806cf8ed3cb729561dddc30c7c40ff08a4cf6095a105fc944d4254501c7edb27d23bee5bf49689c6d5d09442"
                )
            )

            RELEASE -> UserWithMainWalletAndOtf2FA(
                email = "aft.uat.sdex+atm_otf_operation_eighth@gmail.com",
                oAuthSecret = "IYJ46LUNBRGWWTBS",
                otfWallet = OtfWallet(
                    publicKey = "3fc7881c2bdbc182dbd183303c3c27ac84ab499fd83616678b135bdae7b9c82e",
                    secretKey = "e6adee2ff358e4cfcafcb7b94fdf9aa0e14fd768781ad87bb387360ff9fcc4013fc7881c2bdbc182dbd183303c3c27ac84ab499fd83616678b135bdae7b9c82e"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "ef7ad26e656763de0c4bc831e04fd1965149bda00bccb549baf8225a294b4d85",
                    secretKey = "4dd8536b40e2e5db0113f9f7a1b06a9dc1b619c8b27de3623fe23415fb23ba1eef7ad26e656763de0c4bc831e04fd1965149bda00bccb549baf8225a294b4d85"
                )
            )


            PREPROD -> UserWithMainWalletAndOtf2FA(
                email = "aft.uat.sdex+atm_otf_operation_eighth@gmail.com",
                oAuthSecret = "3YJDXLKRR3QH4QZB",
                otfWallet = OtfWallet(
                    publicKey = "9d30bfb11e4f3a808d0d2759209169b3138ecfc5beabbce62afad405be0c46d8",
                    secretKey = "37e328304125b6e8911254b3e46cc5ab9a5a798c82bab1ab84f2f402010416699d30bfb11e4f3a808d0d2759209169b3138ecfc5beabbce62afad405be0c46d8"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "7fec36d278fb9ae1da051cc6a4847344d3422114743a15d54678e5b834fe7d6b",
                    secretKey = "e26ce58da11abe7cff3fb988adfaa09bdf78124fa0619240d557a1f2aaa68c307fec36d278fb9ae1da051cc6a4847344d3422114743a15d54678e5b834fe7d6b"
                )
            )

            PROD -> UserWithMainWalletAndOtf2FA(
                oAuthSecret = "",
                otfWallet = OtfWallet(),
                mainWallet = MainWallet()
            )

            SHARED -> UserWithMainWalletAndOtf2FA(
                email = "aft.uat.sdex+@gmail.com",
                oAuthSecret = "",
                otfWallet = OtfWallet(
                    publicKey = "",
                    secretKey = ""
                ),
                mainWallet = MainWallet(
                    publicKey = "",
                    secretKey = ""
                )
            )
            TOKEN_TRUST -> UserWithMainWalletAndOtf2FA(
                "",
                "",
                otfWallet = OtfWallet(
                    publicKey = "",
                    secretKey = ""
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "",
                    secretKey = ""
                )
            )
        }

        // Atm user: WITHOUT OAuth WITH WALLET. Company: TestCompany01. Industrial
        val ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE02 = when (Environment.stand) {
            DEVELOP -> UserWithMainWalletAndOtf(
                "aft.uat.sdex+universe_02@gmail.com",
                otfWallet = OtfWallet(
                    publicKey = "6ebd7ccfb6109682e262486b9c8866c070f3eef448927fffdf441f8d94c36c99",
                    secretKey = "da1bc90849d89b7b4a37b99f96e60cb771949b7f292a85bea64025d3d353dd0c6ebd7ccfb6109682e262486b9c8866c070f3eef448927fffdf441f8d94c36c99"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "885dd03c3f3b6e97df97583389843e0a0269b07cbb6ff0f8569e3ce341f7d051",
                    secretKey = "c9ca3b9297178c421c91f0a0fa1a67ec2e7f6cad073683efd72ec4959071ba21885dd03c3f3b6e97df97583389843e0a0269b07cbb6ff0f8569e3ce341f7d051"
                )
            )
            RELEASE -> UserWithMainWalletAndOtf(
                "aft.uat.sdex+universe_02@gmail.com",
                otfWallet = OtfWallet(
                    publicKey = "fe7a90d718e85bef5e8f646193e02d8834521bcae8cbda84789a0bd0f12c6fe3",
                    secretKey = "d66e06d011dfdd4e6c48268d826ab76c3132f2f7b383851868d5e18e34cd903efe7a90d718e85bef5e8f646193e02d8834521bcae8cbda84789a0bd0f12c6fe3"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "3bdc462f6e2442a78d7a57d7172bc45f1d8f7dd2418e8688a0583664f6824ad9",
                    secretKey = "15f42ba0974f1914fd10e77b11e27e686c6994b28920e78a7b3327ce94ca3d923bdc462f6e2442a78d7a57d7172bc45f1d8f7dd2418e8688a0583664f6824ad9"
                )
            )
            PREPROD -> UserWithMainWalletAndOtf(
                "aft.uat.sdex+universe_02@gmail.com",
                otfWallet = OtfWallet(
                    publicKey = "fe7a90d718e85bef5e8f646193e02d8834521bcae8cbda84789a0bd0f12c6fe3",
                    secretKey = "d66e06d011dfdd4e6c48268d826ab76c3132f2f7b383851868d5e18e34cd903efe7a90d718e85bef5e8f646193e02d8834521bcae8cbda84789a0bd0f12c6fe3"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "ca5a0218afbf724dce5471f42d2ded521916ea24ff96c40ed725a9b9e6a022de",
                    secretKey = "d7a68dd24a783deb5e3fe313ed6db8b862333c86695cde035fb25a3b5debcd2aca5a0218afbf724dce5471f42d2ded521916ea24ff96c40ed725a9b9e6a022de"
                )
            )
            PROD -> UserWithMainWalletAndOtf(
                otfWallet = OtfWallet(),
                mainWallet = MainWallet()
            )
            SHARED -> UserWithMainWalletAndOtf(
                otfWallet = OtfWallet(),
                mainWallet = MainWallet()
            )
            TOKEN_TRUST -> UserWithMainWalletAndOtf(
                otfWallet = OtfWallet(),
                mainWallet = MainWallet()
            )
        }

        // Atm user: WITHOUT OAuth WITH WALLET. Company: TestCompany01. Industrial
        val ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE03 = when (Environment.stand) {
            DEVELOP -> UserWithMainWalletAndOtf(
                otfWallet = OtfWallet(),
                mainWallet = MainWallet()
            )
            RELEASE -> UserWithMainWalletAndOtf(
                "aft.uat.sdex+universe_03@gmail.com",
                otfWallet = OtfWallet(
                    publicKey = "fe7a90d718e85bef5e8f646193e02d8834521bcae8cbda84789a0bd0f12c6fe3",
                    secretKey = "d66e06d011dfdd4e6c48268d826ab76c3132f2f7b383851868d5e18e34cd903efe7a90d718e85bef5e8f646193e02d8834521bcae8cbda84789a0bd0f12c6fe3"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "3bdc462f6e2442a78d7a57d7172bc45f1d8f7dd2418e8688a0583664f6824ad9",
                    secretKey = "15f42ba0974f1914fd10e77b11e27e686c6994b28920e78a7b3327ce94ca3d923bdc462f6e2442a78d7a57d7172bc45f1d8f7dd2418e8688a0583664f6824ad9"
                )
            )
            PREPROD -> UserWithMainWalletAndOtf(
                "aft.uat.sdex+universe_03@gmail.com",
                otfWallet = OtfWallet(
                    publicKey = "fe7a90d718e85bef5e8f646193e02d8834521bcae8cbda84789a0bd0f12c6fe3",
                    secretKey = "d66e06d011dfdd4e6c48268d826ab76c3132f2f7b383851868d5e18e34cd903efe7a90d718e85bef5e8f646193e02d8834521bcae8cbda84789a0bd0f12c6fe3"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "ca5a0218afbf724dce5471f42d2ded521916ea24ff96c40ed725a9b9e6a022de",
                    secretKey = "d7a68dd24a783deb5e3fe313ed6db8b862333c86695cde035fb25a3b5debcd2aca5a0218afbf724dce5471f42d2ded521916ea24ff96c40ed725a9b9e6a022de"
                )
            )
            PROD -> UserWithMainWalletAndOtf(
                otfWallet = OtfWallet(),
                mainWallet = MainWallet()
            )
            SHARED -> UserWithMainWalletAndOtf(
                otfWallet = OtfWallet(),
                mainWallet = MainWallet()
            )
            TOKEN_TRUST -> UserWithMainWalletAndOtf(
                otfWallet = OtfWallet(),
                mainWallet = MainWallet()
            )
        }

        // Atm user: WITHOUT OAuth WITH WALLET. Company: TestCompany02. Industrial
        val ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE04 = when (Environment.stand) {
            DEVELOP -> UserWithMainWalletAndOtf(
                otfWallet = OtfWallet(),
                mainWallet = MainWallet()
            )
            RELEASE -> UserWithMainWalletAndOtf(
                "aft.uat.sdex+universe_04@gmail.com",
                otfWallet = OtfWallet(
                    publicKey = "6e7fe427c11ba4a540d90a35d415507423661b927ff5eaba240ba1e98c92c949",
                    secretKey = "6794e4df41e820003f84bbb599ad19031e9d0a00c331a4e89c4ac680343fa8bd6e7fe427c11ba4a540d90a35d415507423661b927ff5eaba240ba1e98c92c949"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "3b8acb5865f50a4fa84a333ba23cee025ef41f878d2ee71c7dedb697805a07de",
                    secretKey = "be61dbf843a011e73791cced7fb87a8ccac011fab1a4c2aefbd2c59202fc504f3b8acb5865f50a4fa84a333ba23cee025ef41f878d2ee71c7dedb697805a07de"
                )
            )
            PREPROD -> UserWithMainWalletAndOtf(
                "aft.uat.sdex+universe_04@gmail.com",
                otfWallet = OtfWallet(
                    publicKey = "6e7fe427c11ba4a540d90a35d415507423661b927ff5eaba240ba1e98c92c949",
                    secretKey = "6794e4df41e820003f84bbb599ad19031e9d0a00c331a4e89c4ac680343fa8bd6e7fe427c11ba4a540d90a35d415507423661b927ff5eaba240ba1e98c92c949"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "3b8acb5865f50a4fa84a333ba23cee025ef41f878d2ee71c7dedb697805a07de",
                    secretKey = "be61dbf843a011e73791cced7fb87a8ccac011fab1a4c2aefbd2c59202fc504f3b8acb5865f50a4fa84a333ba23cee025ef41f878d2ee71c7dedb697805a07de"
                )
            )
            PROD -> UserWithMainWalletAndOtf(
                otfWallet = OtfWallet(),
                mainWallet = MainWallet()
            )
            SHARED -> UserWithMainWalletAndOtf(
                otfWallet = OtfWallet(),
                mainWallet = MainWallet()
            )
            TOKEN_TRUST -> UserWithMainWalletAndOtf(
                otfWallet = OtfWallet(),
                mainWallet = MainWallet()
            )
        }

        // Atm user: WITHOUT OAuth WITH WALLET. Company: TestCompany03. Non Industrial
        val ATM_USER_WITHOUT2FA_WITH_WALLET_UNIVERSE05 = when (Environment.stand) {
            DEVELOP -> UserWithMainWalletAndOtf(
                otfWallet = OtfWallet(),
                mainWallet = MainWallet()
            )
            RELEASE -> UserWithMainWalletAndOtf(
                "aft.uat.sdex+universe_05@gmail.com",
                otfWallet = OtfWallet(
                    publicKey = "514cdc8e17ffbf30eb0edadf6883c4bbf3fdccf3c79794fb4eb459ee902a9c8e",
                    secretKey = "25fbe1f666454a0fb23183c238f9582ab11967832164ac2df4e0ad135584783f514cdc8e17ffbf30eb0edadf6883c4bbf3fdccf3c79794fb4eb459ee902a9c8e"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "5bb6e3795f4f3cd93a46f85ab89f3f3bfb8d9ae143db15573024edd747380410",
                    secretKey = "5d83e4ecfc90aef4fb9445fab8afd9f40d21b9f080142b762b5b46d70bde87435bb6e3795f4f3cd93a46f85ab89f3f3bfb8d9ae143db15573024edd747380410"
                )
            )
            PREPROD -> UserWithMainWalletAndOtf(
                "aft.uat.sdex+universe_05@gmail.com",
                otfWallet = OtfWallet(
                    publicKey = "514cdc8e17ffbf30eb0edadf6883c4bbf3fdccf3c79794fb4eb459ee902a9c8e",
                    secretKey = "25fbe1f666454a0fb23183c238f9582ab11967832164ac2df4e0ad135584783f514cdc8e17ffbf30eb0edadf6883c4bbf3fdccf3c79794fb4eb459ee902a9c8e"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "5bb6e3795f4f3cd93a46f85ab89f3f3bfb8d9ae143db15573024edd747380410",
                    secretKey = "5d83e4ecfc90aef4fb9445fab8afd9f40d21b9f080142b762b5b46d70bde87435bb6e3795f4f3cd93a46f85ab89f3f3bfb8d9ae143db15573024edd747380410"
                )
            )
            PROD -> UserWithMainWalletAndOtf(
                otfWallet = OtfWallet(),
                mainWallet = MainWallet()
            )
            SHARED -> UserWithMainWalletAndOtf(
                otfWallet = OtfWallet(),
                mainWallet = MainWallet()
            )
            TOKEN_TRUST -> UserWithMainWalletAndOtf(
                otfWallet = OtfWallet(),
                mainWallet = MainWallet()
            )
        }

        val ATM_USER_2FA_WITH_WALLET_MTEST01 = when (Environment.stand) {
            DEVELOP -> UserWithMainWalletAndOtf2FA(
                "aft.uat.sdex+mtest01@gmail.com",
                "",
                otfWallet = OtfWallet(
                    publicKey = "f5c939b68b57400102bffc52f7ec8cf71176276e47240924ae79af041e0d8b8f",
                    secretKey = "01cd403ee645c2e0e81151790450a7bb562bd87a4b335ee4d6c6c7f7a8d3041af5c939b68b57400102bffc52f7ec8cf71176276e47240924ae79af041e0d8b8f"

                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "8e6d09ebbc4b171dac7918d45b323b4aedef2d4583784237cc6381f17860be72",
                    secretKey = "0c50e32de9ddb806a7a19266b4a724b3efc92deadbb0ed698ab96c307a372af18e6d09ebbc4b171dac7918d45b323b4aedef2d4583784237cc6381f17860be72"
                )
            )
//            RELEASE -> UserWithMainWalletAndOtf(
            RELEASE -> UserWithMainWalletAndOtf2FA(
                "aft.uat.sdex+mtest01@gmail.com",
                "UHY3GNDJPNRNDJV6",
                otfWallet = OtfWallet(
                    publicKey = "d871211ec0d72086841ed641776a7ad6093edf5c9f43017fb98f464431098017",
                    secretKey = "16e9d59cf0c80c41a8403cc4a45c82c56302d13f456a6dfa9a007eb037cce3add871211ec0d72086841ed641776a7ad6093edf5c9f43017fb98f464431098017"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "9423444cefb660374c70e5c776498e281dd619e00e2386fd3bb1e5d064392252",
                    secretKey = "eebfe35b02fc0f4f9cba15cb1882d53bac9d7a4b05c5cf11e7a9884d632758419423444cefb660374c70e5c776498e281dd619e00e2386fd3bb1e5d064392252"
                ),
                castodian = "LLTX6LX5W3BQNRJPJGFQCCJUFO7FJETF"
            )
            PREPROD -> UserWithMainWalletAndOtf2FA(
                "aft.uat.sdex+mtest01@gmail.com",
                "6U4RVYSJK22TINEC",
                otfWallet = OtfWallet(
                    publicKey = "d871211ec0d72086841ed641776a7ad6093edf5c9f43017fb98f464431098017",
                    secretKey = "16e9d59cf0c80c41a8403cc4a45c82c56302d13f456a6dfa9a007eb037cce3add871211ec0d72086841ed641776a7ad6093edf5c9f43017fb98f464431098017"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "9423444cefb660374c70e5c776498e281dd619e00e2386fd3bb1e5d064392252",
                    secretKey = "eebfe35b02fc0f4f9cba15cb1882d53bac9d7a4b05c5cf11e7a9884d632758419423444cefb660374c70e5c776498e281dd619e00e2386fd3bb1e5d064392252"
                )
            )
            PROD -> UserWithMainWalletAndOtf2FA(
                oAuthSecret = "",
                otfWallet = OtfWallet(),
                mainWallet = MainWallet()
            )
            SHARED -> UserWithMainWalletAndOtf2FA(
                oAuthSecret = "",
                otfWallet = OtfWallet(),
                mainWallet = MainWallet()
            )
            TOKEN_TRUST -> UserWithMainWalletAndOtf2FA(
                oAuthSecret = "",
                otfWallet = OtfWallet(),
                mainWallet = MainWallet()
            )
        }

        val ATM_USER_2FA_WITHOUT_WALLET_MTEST02 = when (Environment.stand) {
            DEVELOP -> UserWithMainWalletAndOtf2FA(
                "aft.uat.sdex+mtest02@gmail.com",
                "",
                otfWallet = OtfWallet(
                    publicKey = "",
                    secretKey = ""

                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "",
                    secretKey = ""
                )
            )
//            RELEASE -> UserWithMainWalletAndOtf(
            RELEASE -> UserWithMainWalletAndOtf2FA(
                "aft.uat.sdex+mtest02@gmail.com",
                "",
                otfWallet = OtfWallet(
                    publicKey = "",
                    secretKey = ""
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "",
                    secretKey = ""
                ),
                castodian = "LLTX6LX5W3BQNRJPJGFQCCJUFO7FJETF"
            )
            PREPROD -> UserWithMainWalletAndOtf2FA(
                "aft.uat.sdex+mtest02@gmail.com",
                oAuthSecret = "",
                otfWallet = OtfWallet(),
                mainWallet = MainWallet()
            )
            PROD -> UserWithMainWalletAndOtf2FA(
                oAuthSecret = "",
                otfWallet = OtfWallet(),
                mainWallet = MainWallet()
            )
            SHARED -> UserWithMainWalletAndOtf2FA(
                oAuthSecret = "",
                otfWallet = OtfWallet(),
                mainWallet = MainWallet()
            )
            TOKEN_TRUST -> UserWithMainWalletAndOtf2FA(
                oAuthSecret = "",
                otfWallet = OtfWallet(),
                mainWallet = MainWallet()
            )
        }

        val ATM_USER_2FA_WITH_WALLET_MTEST03 = when (Environment.stand) {
            DEVELOP -> UserWithMainWalletAndOtf2FA(
                "aft.uat.sdex+testrel888@gmail.com",
                "",
                otfWallet = OtfWallet(
                    publicKey = "",
                    secretKey = ""

                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "",
                    secretKey = ""
                )
            )
//            RELEASE -> UserWithMainWalletAndOtf(
            RELEASE -> UserWithMainWalletAndOtf2FA(
                "aft.uat.sdex+testrel888@gmail.com",
                "",
                otfWallet = OtfWallet(
                    publicKey = "502962b57f850f7b8960d1bacc62ee4e7e7d41be848aa4e2ee16a3254b7150a8",
                    secretKey = "e1152a3175a8233ec2d38a2e735966a9453353054a7e662e0bec98ec85c3e8e0502962b57f850f7b8960d1bacc62ee4e7e7d41be848aa4e2ee16a3254b7150a8"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "e216e7b635dc0002e276083d24c7f388ac83266120e21d04ace2ff7d9afd64ba",
                    secretKey = "f7e71b0164d3468c1175eed57bf1f3bca8b1ae898a9e356a153d1c0a939c9061e216e7b635dc0002e276083d24c7f388ac83266120e21d04ace2ff7d9afd64ba"
                ),
                castodian = ""
            )
            PREPROD -> UserWithMainWalletAndOtf2FA(
                "aft.uat.sdex+testrel888@gmail.com",
                "",
                otfWallet = OtfWallet(
                    publicKey = "",
                    secretKey = ""
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "",
                    secretKey = ""
                )
            )
            PROD -> UserWithMainWalletAndOtf2FA(
                oAuthSecret = "",
                otfWallet = OtfWallet(),
                mainWallet = MainWallet()
            )
            SHARED -> UserWithMainWalletAndOtf2FA(
                oAuthSecret = "",
                otfWallet = OtfWallet(),
                mainWallet = MainWallet()
            )
            TOKEN_TRUST -> UserWithMainWalletAndOtf2FA(
                oAuthSecret = "",
                otfWallet = OtfWallet(),
                mainWallet = MainWallet()
            )
        }

        val ATM_ADMIN = when (Environment.stand) {
            DEVELOP -> DefaultUserWithCustomPassword(
                "admin@n-t.io",
                password = "12341234"
            )
            RELEASE -> DefaultUserWithCustomPassword(
                "admin@n-t.io",
                password = "12341234"
            )
            PREPROD -> DefaultUserWithCustomPassword(
                "admin@n-t.io",
                password = "12341234"
            )
            PROD -> DefaultUserWithCustomPassword(
                "admin@n-t.io",
                password = "12341234"
            )
            SHARED -> DefaultUserWithCustomPassword(
                "admin@n-t.io",
                password = "12341234"
            )
            TOKEN_TRUST -> DefaultUserWithCustomPassword(
                "krasnikov.k@n-t.io",
                password = "DerParol789!ABC"
            )
        }

        // ATM_USER_EMPLOYEE_ADMIN_ROLE
        // Atm user: Employee ADMIN Role
        val ATM_USER_EMPLOYEE_ADMIN_ROLE = when (Environment.stand) {
            DEVELOP -> UserWithMainWalletAndOtf2FA(
                oAuthSecret = "",
                mainWallet = MainWallet(),
                otfWallet = OtfWallet()
            )
            RELEASE -> UserWithMainWalletAndOtf2FA(
                email = "aft.uat.sdex+atm_employee_admin@gmail.com",
                oAuthSecret = "2T4FW3Z2GAPTQGLI",
                mainWallet = MainWallet(
                    name = "Admin",
                    publicKey = "e0112309fbca748fdad6bacdd9980ebbdfe766e16bb8c8f5d5f90755e6c0ec02",
                    secretKey = ""
                ),
                otfWallet = OtfWallet(
                    name = "AdminOTF",
                    publicKey = "ae8318db097038b06849cb3e2d62f1293a853ee76530be3841be46228a31497c",
                    secretKey = "541c104569d6ed0bf22eb103458dc07bb5456765349a735f9bc9830b040f8aa9ae8318db097038b06849cb3e2d62f1293a853ee76530be3841be46228a31497c"
                )
            )
            PREPROD -> UserWithMainWalletAndOtf2FA(
                email = "aft.uat.sdex+atm_employee_admin@gmail.com",
                oAuthSecret = "ABLFVXCWRAM3CYGR",
                mainWallet = MainWallet(
                    name = "Admin",
                    publicKey = "d56708db5d1d71b10ccd5d8846b8628ac9fe9085e7db11a7aab4d549e21dc142",
                    secretKey = "cee32cf125e5a92d7059bc4cae1cbd3a31a8d2dea175c319b8e766603c450411d56708db5d1d71b10ccd5d8846b8628ac9fe9085e7db11a7aab4d549e21dc142"
                ),
                otfWallet = OtfWallet(
                    name = "AdminOTF",
                    publicKey = "1325167059a0839ed02818eaeccf7d661945eed470edc4f739b2d8868b528523",
                    secretKey = "0034c2ab03cfbda4ff62bf00ab15089e0c347ed9170c0c779b35803856e494661325167059a0839ed02818eaeccf7d661945eed470edc4f739b2d8868b528523"
                )
            )
            PROD -> UserWithMainWalletAndOtf2FA(
                oAuthSecret = "",
                mainWallet = MainWallet(),
                otfWallet = OtfWallet()
            )
            SHARED -> UserWithMainWalletAndOtf2FA(
                email = "aft.uat.sdex+atm_employeeadmin@gmail.com",
                oAuthSecret = "IFWLVERDLDIXI3TA",
                mainWallet = MainWallet(
                    name = "Admin",
                    publicKey = "118573eb9b3209009f1eb910ea5cf896faef87be03602118cc281b2e3e376f26",
                    secretKey = "9d1864f67af506c87caa9168d48678d80808deb1d74d0ff9268c216a74b4ceef118573eb9b3209009f1eb910ea5cf896faef87be03602118cc281b2e3e376f26"
                ),
                otfWallet = OtfWallet(
                    name = "AdminOTF",
                    publicKey = "3d95aaf815e2dc036288af247aa989a9f7eb9068792d1139a2138b4fe619e0c6",
                    secretKey = "55a09c184fd7cea9021b5e90cd5b37ed724321ad574785517d5d2f16c327a0e13d95aaf815e2dc036288af247aa989a9f7eb9068792d1139a2138b4fe619e0c6"
                )
            )
            TOKEN_TRUST -> UserWithMainWalletAndOtf2FA(
                oAuthSecret = "",
                mainWallet = MainWallet(),
                otfWallet = OtfWallet()
            )
        }

        val ATM_USER_VALIDATOR_WITHOUT_FUNDS = when (Environment.stand) {
            DEVELOP -> UserWithMultipleMainWallet2FA(
                "", "",
                walletList = listOf(
                    MainWallet(
                        name = "",
                        publicKey = "",
                        secretKey = "",
                        walletId = ""
                    )
                )
            )
            RELEASE -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+validator_without_funds@gmail.com", "",
                walletList = listOf(
                    MainWallet(
                        name = "Main 1",
                        publicKey = "5e130513e46b0a2a04d376b09b5e3b61d47dcb617cbe3d60e40ccd4472e33afc",
                        secretKey = "47d27abcbfbb20af6317de31063fabcce09bfad08bd435b2b012206fdae70f175e130513e46b0a2a04d376b09b5e3b61d47dcb617cbe3d60e40ccd4472e33afc",
                        walletId = ""
                    )
                )
            )
            PREPROD -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+validator_without_funds@gmail.com", "",
                walletList = listOf(
                    MainWallet(
                        name = "Main 1",
                        publicKey = "1ce84b538d5db71469ab97fe1fa83060ce5803161d962ab8eb3990204ecbe376",
                        secretKey = "19d2a1a21ef5462c67f4bee3510b946d392b15646616eb337a4e12b84e10135f1ce84b538d5db71469ab97fe1fa83060ce5803161d962ab8eb3990204ecbe376",
                        walletId = ""
                    )
                )
            )
            PROD -> UserWithMultipleMainWallet2FA(
                "", "",
                walletList = listOf(
                    MainWallet(
                        name = "",
                        publicKey = "",
                        secretKey = "",
                        walletId = ""
                    )
                )
            )
            SHARED -> UserWithMultipleMainWallet2FA(
                "", "",
                walletList = listOf(
                    MainWallet(
                        name = "",
                        publicKey = "",
                        secretKey = "",
                        walletId = ""
                    )
                )
            )
            TOKEN_TRUST -> UserWithMultipleMainWallet2FA(
                "", "",
                walletList = listOf(
                    MainWallet(
                        name = "",
                        publicKey = "",
                        secretKey = "",
                        walletId = ""
                    )
                )
            )
        }

        val ATM_USER_VALIDATOR_WITHOUT_2FA = when (Environment.stand) {
            DEVELOP -> UserWithMultipleMainWallet2FA(
                "", "",
                walletList = listOf(
                    MainWallet(
                        name = "",
                        publicKey = "",
                        secretKey = "",
                        walletId = ""
                    )
                )
            )
            RELEASE -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+validator_without_2fa@gmail.com",
                "",
                walletList = listOf(
                    MainWallet(
                        name = "Main 1",
                        publicKey = "cfd217173da4595435e6c77e2849ba9e5fe7a040282d34364d2cab11a00ec6ed",
                        secretKey = "7ec606f9d6de6c2ac62ad0d06772e90c92f26f4d18b46b9ae51175a9bb7f4d28cfd217173da4595435e6c77e2849ba9e5fe7a040282d34364d2cab11a00ec6ed",
                        walletId = "2mNSFggVZCWAoyhY4QgSptujb5u9vXzW43XoJvkJqWrQmcpGFX"
                    )
                )
            )
            PREPROD -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+validator_without_2fa@gmail.com", "",
                walletList = listOf(
                    MainWallet(
                        name = "Main 1",
                        publicKey = "1cba1babaf5ea5bf59cabefcd734f6fce28e772b211750ee942d15ce8dc2a858",
                        secretKey = "e23c5191e1bb31e0daa023388ea8804e553fb30a75edc7c8c45f49d87f97a00f1cba1babaf5ea5bf59cabefcd734f6fce28e772b211750ee942d15ce8dc2a858",
                        walletId = ""
                    )
                )
            )
            PROD -> UserWithMultipleMainWallet2FA(
                "", "",
                walletList = listOf(
                    MainWallet(
                        name = "",
                        publicKey = "",
                        secretKey = "",
                        walletId = ""
                    )
                )
            )
            SHARED -> UserWithMultipleMainWallet2FA(
                "", "",
                walletList = listOf(
                    MainWallet(
                        name = "",
                        publicKey = "",
                        secretKey = "",
                        walletId = ""
                    )
                )
            )
            TOKEN_TRUST -> UserWithMultipleMainWallet2FA(
                "", "",
                walletList = listOf(
                    MainWallet(
                        name = "",
                        publicKey = "",
                        secretKey = "",
                        walletId = ""
                    )
                )
            )
        }

        //
        val ATM_USER_VALIDATOR_2FA = when (Environment.stand) {
            DEVELOP -> UserWithMultipleMainWallet2FA(
                "", "",
                walletList = listOf(
                    MainWallet(
                        name = "",
                        publicKey = "",
                        secretKey = "",
                        walletId = ""
                    )
                )
            )
            RELEASE -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+validator_2fa@gmail.com",
                "LMKXTZ3PQIT5GLBJ",
                walletList = listOf(
                    MainWallet(
                        name = "Main 1",
                        publicKey = "5f2c7a0615194a7f7579801396e7023d66a95bed7ca5144dd6a499e3d6076376",
                        secretKey = "034ba5c34df92e968999482b40e8bbaf8daf8807b47ba67e314735cc297c9ac85f2c7a0615194a7f7579801396e7023d66a95bed7ca5144dd6a499e3d6076376",
                        walletId = ""
                    )
                )
            )
            PREPROD -> UserWithMultipleMainWallet2FA(
                "aft.uat.sdex+validator_2fa@gmail.com", "JW5T67RXRWJ33BXX",
                walletList = listOf(
                    MainWallet(
                        name = "Main 1",
                        publicKey = "29fb62f5338405234fe3c3713237fde307a913dbd1aedc428f1412de5ea96483",
                        secretKey = "d0a46fbb7752f31be440c4d43b1378aa302f3f003b9d8f4cbc29c5af2875949d29fb62f5338405234fe3c3713237fde307a913dbd1aedc428f1412de5ea96483",
                        walletId = ""
                    )
                )
            )
            PROD -> UserWithMultipleMainWallet2FA(
                "", "",
                walletList = listOf(
                    MainWallet(
                        name = "",
                        publicKey = "",
                        secretKey = "",
                        walletId = ""
                    )
                )
            )
            SHARED -> UserWithMultipleMainWallet2FA(
                "", "",
                walletList = listOf(
                    MainWallet(
                        name = "",
                        publicKey = "",
                        secretKey = "",
                        walletId = ""
                    )
                )
            )
            TOKEN_TRUST -> UserWithMultipleMainWallet2FA(
                "", "",
                walletList = listOf(
                    MainWallet(
                        name = "",
                        publicKey = "",
                        secretKey = "",
                        walletId = ""
                    )
                )
            )
        }

        val ATM_USER_NOT_VALIDATOR = when (Environment.stand) {
            DEVELOP -> DefaultUser()
            RELEASE -> DefaultUser("aft.uat.sdex+not_validator@gmail.com")
            PREPROD -> DefaultUser("aft.uat.sdex+not_validator@gmail.com")
            PROD -> DefaultUser()
            SHARED -> DefaultUser("")
            TOKEN_TRUST -> DefaultUser()
        }


        // Atm user: Employee MANAGER Role
        val ATM_USER_EMPLOYEE_MANAGER_ROLE = when (Environment.stand) {
            DEVELOP -> DefaultUser()
            RELEASE -> DefaultUser("aft.uat.sdex+atm_employeemanager@gmail.com")
            PREPROD -> DefaultUser("aft.uat.sdex+atm_employeemanager@gmail.com")
            PROD -> DefaultUser()
            SHARED -> DefaultUser("aft.uat.sdex+atm_employeemanager@gmail.com")
            TOKEN_TRUST -> DefaultUser()
        }

        //Atm user: Employee user(Participant) with ETC company and ETC tokens
        val ATM_USER_FOR_ETC_COMPANY_WITH_TOKENS = when (Environment.stand) {
            DEVELOP -> DefaultUser()
            RELEASE -> DefaultUser("aft.uat.sdex+release_etc_company_employee@gmail.com")
            PREPROD -> DefaultUser("aft.uat.sdex+etc_company_employee@gmail.com")
            PROD -> DefaultUser()
            SHARED -> DefaultUser("")
            TOKEN_TRUST -> DefaultUser()
        }

        //Atm user: Employee user(Participant) for Industrial Tokens request
        val ATM_USER_FOR_INDUSTRIAL_COMPANY = when (Environment.stand) {
            DEVELOP -> UserWithMainWalletAndOtf(
                otfWallet = OtfWallet(),
                mainWallet = MainWallet()
            )
            RELEASE -> UserWithMainWalletAndOtf(
                "aft.uat.sdex+release_industrial_comapny@gmail.com",
                MainWallet(
                    name = "Main 1",
                    publicKey = "09bc15d8fea83115ff42776f21acd3d5da6277f79279904e72c868fcfe850ba3",
                    secretKey = "67e1b4dd42ce4450105c2c2a5fbe0385b55f36162c74b2040c57ec6430c1450a09bc15d8fea83115ff42776f21acd3d5da6277f79279904e72c868fcfe850ba3"
                ),
                OtfWallet(
                    name = "",
                    publicKey = "",
                    secretKey = ""
                )
            )
            PREPROD -> UserWithMainWalletAndOtf(
                "aft.uat.sdex+preprod_industrial_comapny@gmail.com",
                MainWallet(
                    name = "Main 1",
                    publicKey = "4be53bcb06ecd5a14a11015af632753aff45c44623e297bfcc651389ca90c396",
                    secretKey = "dfc190a8af65b9cecd9601ad1bafea07cb462cd1642449f7f609319c49fb34714be53bcb06ecd5a14a11015af632753aff45c44623e297bfcc651389ca90c396"
                ),
                OtfWallet(
                    name = "",
                    publicKey = "",
                    secretKey = ""
                )
            )
            PROD -> UserWithMainWalletAndOtf(
                otfWallet = OtfWallet(),
                mainWallet = MainWallet()
            )
            SHARED -> UserWithMainWalletAndOtf(
                otfWallet = OtfWallet(),
                mainWallet = MainWallet()
            )
            TOKEN_TRUST -> UserWithMainWalletAndOtf(
                otfWallet = OtfWallet(),
                mainWallet = MainWallet()
            )
        }
    }
    //User for RFQ

            val ATM_USER_2FA_WITH_WALLET_MTEST03 = when (Environment.stand) {
            DEVELOP -> UserWithMainWalletAndOtf2FA(
                "aft.uat.sdex+testRel888@gmail.com",
                "",
                otfWallet = OtfWallet(
                    publicKey = "",
                    secretKey = ""

                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "",
                    secretKey = ""
                )
            )
//            RELEASE -> UserWithMainWalletAndOtf(
            RELEASE -> UserWithMainWalletAndOtf2FA(
                "aft.uat.sdex+testRel888@gmail.com",
                "",
                otfWallet = OtfWallet(
                    publicKey = "502962b57f850f7b8960d1bacc62ee4e7e7d41be848aa4e2ee16a3254b7150a8",
                    secretKey = "e1152a3175a8233ec2d38a2e735966a9453353054a7e662e0bec98ec85c3e8e0502962b57f850f7b8960d1bacc62ee4e7e7d41be848aa4e2ee16a3254b7150a8"
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "e216e7b635dc0002e276083d24c7f388ac83266120e21d04ace2ff7d9afd64ba",
                    secretKey = "f7e71b0164d3468c1175eed57bf1f3bca8b1ae898a9e356a153d1c0a939c9061e216e7b635dc0002e276083d24c7f388ac83266120e21d04ace2ff7d9afd64ba"
                ),
                castodian = ""
            )
            PREPROD -> UserWithMainWalletAndOtf2FA(
                "aft.uat.sdex+testRel888@gmail.com",
                "",
                otfWallet = OtfWallet(
                    publicKey = "",
                    secretKey = ""
                ),
                mainWallet = MainWallet(
                    name = "Main 1",
                    publicKey = "",
                    secretKey = ""
                )
            )
            PROD -> UserWithMainWalletAndOtf2FA(
                oAuthSecret = "",
                otfWallet = OtfWallet(),
                mainWallet = MainWallet()
            )
            SHARED -> UserWithMainWalletAndOtf2FA(
                oAuthSecret = "",
                otfWallet = OtfWallet(),
                mainWallet = MainWallet()
            )
            TOKEN_TRUST -> UserWithMainWalletAndOtf2FA(
                oAuthSecret = "",
                otfWallet = OtfWallet(),
                mainWallet = MainWallet()
            )
        }

    enum class Stand {
        DEVELOP, RELEASE, PREPROD, PROD, SHARED, TOKEN_TRUST
    }

}

