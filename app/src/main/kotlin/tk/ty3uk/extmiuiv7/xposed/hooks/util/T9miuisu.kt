package tk.ty3uk.extmiuiv7.xposed.hooks.util

import java.util.HashSet

object T9miuisu {
    private val sNumberCeroMap = charArrayOf('?')
    private val sNumberCeroSet = createSet(sNumberCeroMap)
    private val sNumberEightMap: CharArray
    private val sNumberEightSet: Set<Char>
    private val sNumberFiveMap: CharArray
    private val sNumberFiveSet: Set<Char>
    private val sNumberFourMap: CharArray
    private val sNumberFourSet: Set<Char>
    private val sNumberNineMap: CharArray
    private val sNumberNineSet: Set<Char>
    private val sNumberOneMap = charArrayOf(12595.toChar(), 12619.toChar())
    private val sNumberOneSet = createSet(sNumberOneMap)
    private val sNumberSevenMap: CharArray
    private val sNumberSevenSet: Set<Char>
    private val sNumberSixMap: CharArray
    private val sNumberSixSet: Set<Char>
    private val sNumberThreeMap: CharArray
    private val sNumberThreeSet: Set<Char>
    private val sNumberTwoMap = charArrayOf(192.toChar(), 193.toChar(), 195.toChar(), 196.toChar(), 197.toChar(), 198.toChar(), 199.toChar(), 224.toChar(), 225.toChar(), 226.toChar(), 227.toChar(), 228.toChar(), 229.toChar(), 230.toChar(), 231.toChar(), 256.toChar(), 257.toChar(), 258.toChar(), 259.toChar(), 260.toChar(), 261.toChar(), 262.toChar(), 263.toChar(), 264.toChar(), 265.toChar(), 266.toChar(), 267.toChar(), 268.toChar(), 269.toChar(), 384.toChar(), 385.toChar(), 386.toChar(), 387.toChar(), 388.toChar(), 389.toChar(), 390.toChar(), 391.toChar(), 392.toChar(), 461.toChar(), 462.toChar(), 902.toChar(), 913.toChar(), 914.toChar(), 915.toChar(), 940.toChar(), 945.toChar(), 946.toChar(), 947.toChar(), 1040.toChar(), 1041.toChar(), 1042.toChar(), 1043.toChar(), 1072.toChar(), 1073.toChar(), 1074.toChar(), 1075.toChar(), 1168.toChar(), 1169.toChar(), 7840.toChar(), 7841.toChar(), 7842.toChar(), 7843.toChar(), 7844.toChar(), 7845.toChar(), 7846.toChar(), 7847.toChar(), 7848.toChar(), 7849.toChar(), 7850.toChar(), 7851.toChar(), 7852.toChar(), 7853.toChar(), 7854.toChar(), 7855.toChar(), 176.toChar(), 177.toChar(), 178.toChar(), 179.toChar(), 180.toChar(), 181.toChar(), 182.toChar(), 183.toChar(), 12596.toChar(), 1576.toChar(), 1577.toChar(), 1578.toChar(), 1579.toChar(), 1662.toChar(), 1491.toChar(), 1492.toChar(), 1493.toChar())
    private val sNumberTwoSet = createSet(sNumberTwoMap)
    private val sPinyinT9Map = charArrayOf(50.toChar(), 50.toChar(), 50.toChar(), 51.toChar(), 51.toChar(), 51.toChar(), 52.toChar(), 52.toChar(), 52.toChar(), 53.toChar(), 53.toChar(), 53.toChar(), 54.toChar(), 54.toChar(), 54.toChar(), 55.toChar(), 55.toChar(), 55.toChar(), 55.toChar(), 56.toChar(), 56.toChar(), 56.toChar(), 57.toChar(), 57.toChar(), 57.toChar(), 57.toChar())
    private val sZhuyin2T9Map = charArrayOf(49.toChar(), 49.toChar(), 49.toChar(), 49.toChar(), 50.toChar(), 50.toChar(), 50.toChar(), 50.toChar(), 51.toChar(), 51.toChar(), 51.toChar(), 52.toChar(), 52.toChar(), 52.toChar(), 53.toChar(), 53.toChar(), 53.toChar(), 53.toChar(), 54.toChar(), 54.toChar(), 54.toChar(), 55.toChar(), 55.toChar(), 55.toChar(), 55.toChar(), 56.toChar(), 56.toChar(), 56.toChar(), 56.toChar(), 57.toChar(), 57.toChar(), 57.toChar(), 57.toChar(), 57.toChar(), 48.toChar(), 48.toChar(), 48.toChar())

    init {
        sNumberThreeMap = charArrayOf(200.toChar(), 201.toChar(), 202.toChar(), 203.toChar(), 232.toChar(), 233.toChar(), 234.toChar(), 235.toChar(), 270.toChar(), 271.toChar(), 272.toChar(), 273.toChar(), 274.toChar(), 275.toChar(), 276.toChar(), 277.toChar(), 278.toChar(), 279.toChar(), 280.toChar(), 281.toChar(), 282.toChar(), 283.toChar(), 393.toChar(), 394.toChar(), 395.toChar(), 396.toChar(), 397.toChar(), 398.toChar(), 399.toChar(), 400.toChar(), 401.toChar(), 402.toChar(), 916.toChar(), 917.toChar(), 918.toChar(), 941.toChar(), 948.toChar(), 949.toChar(), 950.toChar(), 7864.toChar(), 7865.toChar(), 7866.toChar(), 7867.toChar(), 7868.toChar(), 7869.toChar(), 7870.toChar(), 7871.toChar(), 7872.toChar(), 7873.toChar(), 7874.toChar(), 7875.toChar(), 7876.toChar(), 7877.toChar(), 7878.toChar(), 7879.toChar(), 12599.toChar(), 12620.toChar(), 1025.toChar(), 1026.toChar(), 1028.toChar(), 1044.toChar(), 1045.toChar(), 1046.toChar(), 1047.toChar(), 1076.toChar(), 1077.toChar(), 1078.toChar(), 1079.toChar(), 1105.toChar(), 1106.toChar(), 1108.toChar(), 1488.toChar(), 1489.toChar(), 1490.toChar(), 1569.toChar(), 1570.toChar(), 1571.toChar(), 1573.toChar(), 1575.toChar(), 1609.toChar())
        sNumberThreeSet = createSet(sNumberThreeMap)
        sNumberFourMap = charArrayOf(204.toChar(), 205.toChar(), 207.toChar(), 236.toChar(), 237.toChar(), 239.toChar(), 284.toChar(), 285.toChar(), 286.toChar(), 287.toChar(), 288.toChar(), 289.toChar(), 290.toChar(), 291.toChar(), 292.toChar(), 293.toChar(), 294.toChar(), 295.toChar(), 296.toChar(), 297.toChar(), 298.toChar(), 299.toChar(), 300.toChar(), 301.toChar(), 302.toChar(), 303.toChar(), 304.toChar(), 305.toChar(), 306.toChar(), 307.toChar(), 403.toChar(), 404.toChar(), 405.toChar(), 406.toChar(), 407.toChar(), 463.toChar(), 464.toChar(), 918.toChar(), 919.toChar(), 920.toChar(), 921.toChar(), 938.toChar(), 942.toChar(), 943.toChar(), 951.toChar(), 952.toChar(), 953.toChar(), 970.toChar(), 1030.toChar(), 1031.toChar(), 1032.toChar(), 1048.toChar(), 1049.toChar(), 1050.toChar(), 1051.toChar(), 1080.toChar(), 1081.toChar(), 1082.toChar(), 1083.toChar(), 1110.toChar(), 1111.toChar(), 1112.toChar(), 1501.toChar(), 1502.toChar(), 1503.toChar(), 1504.toChar(), 1587.toChar(), 1588.toChar(), 1589.toChar(), 1590.toChar(), 7880.toChar(), 7881.toChar(), 7882.toChar(), 7883.toChar(), 12601.toChar())
        sNumberFourSet = createSet(sNumberFourMap)
        sNumberFiveMap = charArrayOf(308.toChar(), 309.toChar(), 310.toChar(), 311.toChar(), 312.toChar(), 313.toChar(), 314.toChar(), 315.toChar(), 316.toChar(), 317.toChar(), 318.toChar(), 319.toChar(), 320.toChar(), 321.toChar(), 322.toChar(), 408.toChar(), 409.toChar(), 410.toChar(), 411.toChar(), 922.toChar(), 923.toChar(), 924.toChar(), 954.toChar(), 955.toChar(), 956.toChar(), 1052.toChar(), 1053.toChar(), 1054.toChar(), 1055.toChar(), 1084.toChar(), 1085.toChar(), 1086.toChar(), 1087.toChar(), 1497.toChar(), 1498.toChar(), 1499.toChar(), 1500.toChar(), 1583.toChar(), 1584.toChar(), 1585.toChar(), 1586.toChar(), 1688.toChar(), 12609.toChar())
        sNumberFiveSet = createSet(sNumberFiveMap)
        sNumberSixMap = charArrayOf(209.toChar(), 210.toChar(), 211.toChar(), 212.toChar(), 213.toChar(), 214.toChar(), 215.toChar(), 216.toChar(), 241.toChar(), 242.toChar(), 243.toChar(), 244.toChar(), 245.toChar(), 246.toChar(), 247.toChar(), 248.toChar(), 323.toChar(), 324.toChar(), 325.toChar(), 326.toChar(), 327.toChar(), 328.toChar(), 329.toChar(), 330.toChar(), 331.toChar(), 332.toChar(), 333.toChar(), 334.toChar(), 335.toChar(), 336.toChar(), 337.toChar(), 338.toChar(), 339.toChar(), 412.toChar(), 413.toChar(), 414.toChar(), 415.toChar(), 416.toChar(), 417.toChar(), 418.toChar(), 419.toChar(), 908.toChar(), 925.toChar(), 926.toChar(), 927.toChar(), 957.toChar(), 958.toChar(), 959.toChar(), 972.toChar(), 1056.toChar(), 1057.toChar(), 1058.toChar(), 1059.toChar(), 1088.toChar(), 1089.toChar(), 1090.toChar(), 1091.toChar(), 1494.toChar(), 1495.toChar(), 1496.toChar(), 1580.toChar(), 1581.toChar(), 1582.toChar(), 1670.toChar(), 7884.toChar(), 7885.toChar(), 7886.toChar(), 7887.toChar(), 7888.toChar(), 7889.toChar(), 7890.toChar(), 7891.toChar(), 7892.toChar(), 7893.toChar(), 7894.toChar(), 7895.toChar(), 7896.toChar(), 7897.toChar(), 7898.toChar(), 7899.toChar(), 7900.toChar(), 7901.toChar(), 7902.toChar(), 7903.toChar(), 7904.toChar(), 7905.toChar(), 7906.toChar(), 7907.toChar(), 12610.toChar(), 12621.toChar())
        sNumberSixSet = createSet(sNumberSixMap)
        sNumberSevenMap = charArrayOf(223.toChar(), 340.toChar(), 341.toChar(), 342.toChar(), 343.toChar(), 344.toChar(), 345.toChar(), 346.toChar(), 347.toChar(), 348.toChar(), 349.toChar(), 350.toChar(), 351.toChar(), 352.toChar(), 353.toChar(), 420.toChar(), 421.toChar(), 422.toChar(), 423.toChar(), 424.toChar(), 425.toChar(), 426.toChar(), 928.toChar(), 929.toChar(), 931.toChar(), 960.toChar(), 961.toChar(), 962.toChar(), 963.toChar(), 1060.toChar(), 1061.toChar(), 1062.toChar(), 1063.toChar(), 1092.toChar(), 1093.toChar(), 1094.toChar(), 1095.toChar(), 1512.toChar(), 1513.toChar(), 1514.toChar(), 1572.toChar(), 1574.toChar(), 1606.toChar(), 1607.toChar(), 1608.toChar(), 1610.toChar(), 1740.toChar(), 12613.toChar())
        sNumberSevenSet = createSet(sNumberSevenMap)
        sNumberEightMap = charArrayOf(217.toChar(), 218.toChar(), 219.toChar(), 220.toChar(), 249.toChar(), 250.toChar(), 251.toChar(), 252.toChar(), 354.toChar(), 355.toChar(), 356.toChar(), 357.toChar(), 358.toChar(), 359.toChar(), 360.toChar(), 361.toChar(), 362.toChar(), 363.toChar(), 364.toChar(), 365.toChar(), 366.toChar(), 367.toChar(), 368.toChar(), 369.toChar(), 370.toChar(), 371.toChar(), 427.toChar(), 428.toChar(), 429.toChar(), 430.toChar(), 431.toChar(), 432.toChar(), 433.toChar(), 434.toChar(), 467.toChar(), 468.toChar(), 469.toChar(), 470.toChar(), 471.toChar(), 472.toChar(), 473.toChar(), 474.toChar(), 475.toChar(), 476.toChar(), 932.toChar(), 933.toChar(), 934.toChar(), 964.toChar(), 965.toChar(), 966.toChar(), 971.toChar(), 1064.toChar(), 1065.toChar(), 1066.toChar(), 1067.toChar(), 1096.toChar(), 1097.toChar(), 1098.toChar(), 1099.toChar(), 1509.toChar(), 1510.toChar(), 1511.toChar(), 1601.toChar(), 1602.toChar(), 1603.toChar(), 1604.toChar(), 1605.toChar(), 1705.toChar(), 1711.toChar(), 7908.toChar(), 7909.toChar(), 7910.toChar(), 7911.toChar(), 7912.toChar(), 7913.toChar(), 7914.toChar(), 7915.toChar(), 7916.toChar(), 7917.toChar(), 7918.toChar(), 7919.toChar(), 7920.toChar(), 7921.toChar(), 12615.toChar())
        sNumberEightSet = createSet(sNumberEightMap)
        sNumberNineMap = charArrayOf(221.toChar(), 253.toChar(), 372.toChar(), 373.toChar(), 374.toChar(), 375.toChar(), 376.toChar(), 377.toChar(), 378.toChar(), 379.toChar(), 380.toChar(), 381.toChar(), 382.toChar(), 435.toChar(), 436.toChar(), 437.toChar(), 438.toChar(), 439.toChar(), 440.toChar(), 441.toChar(), 442.toChar(), 443.toChar(), 444.toChar(), 445.toChar(), 446.toChar(), 447.toChar(), 911.toChar(), 935.toChar(), 936.toChar(), 937.toChar(), 967.toChar(), 968.toChar(), 969.toChar(), 974.toChar(), 1068.toChar(), 1069.toChar(), 1070.toChar(), 1071.toChar(), 1100.toChar(), 1101.toChar(), 1102.toChar(), 1103.toChar(), 1505.toChar(), 1506.toChar(), 1507.toChar(), 1508.toChar(), 1591.toChar(), 1592.toChar(), 1593.toChar(), 1594.toChar(), 7922.toChar(), 7923.toChar(), 7924.toChar(), 7925.toChar(), 7926.toChar(), 7927.toChar(), 7928.toChar(), 7929.toChar(), 12616.toChar(), 12618.toChar())
        sNumberNineSet = createSet(sNumberNineMap)
    }

    private fun createSet(paramArrayOfChar: CharArray): Set<Char> {
        val localHashSet = HashSet<Char>()
        val i = paramArrayOfChar.size
        var j = 0
        while (true) {
            if (j >= i) {
                return localHashSet
            }
            localHashSet.add(Character.valueOf(paramArrayOfChar[j]))
            j++
        }
    }

    fun formatCharToT9(paramChar: Char): Char {
        if (isValidT9Key(paramChar)) {
            return paramChar
        }
        if (paramChar >= 'A' && paramChar <= 'Z') {
            return sPinyinT9Map[paramChar - 'A']
        }
        if (paramChar >= 'a' && paramChar <= 'z') {
            return sPinyinT9Map[paramChar - 'a']
        }
        if (paramChar >= '?' && paramChar <= '?') {
            return sZhuyin2T9Map[paramChar - '?']
        }
        if (sNumberCeroSet.contains(Character.valueOf(paramChar))) {
            return '0'
        }
        if (sNumberOneSet.contains(Character.valueOf(paramChar))) {
            return '1'
        }
        if (sNumberTwoSet.contains(Character.valueOf(paramChar))) {
            return '2'
        }
        if (sNumberThreeSet.contains(Character.valueOf(paramChar))) {
            return '3'
        }
        if (sNumberFourSet.contains(Character.valueOf(paramChar))) {
            return '4'
        }
        if (sNumberFiveSet.contains(Character.valueOf(paramChar))) {
            return '5'
        }
        if (sNumberSixSet.contains(Character.valueOf(paramChar))) {
            return '6'
        }
        if (sNumberSevenSet.contains(Character.valueOf(paramChar))) {
            return '7'
        }
        if (sNumberEightSet.contains(Character.valueOf(paramChar))) {
            return '8'
        }
        if (sNumberNineSet.contains(Character.valueOf(paramChar))) {
            return '9'
        }
        return '\u0000'
    }

    fun isValidT9Key(paramChar: Char): Boolean {
        return paramChar >= '0' && paramChar <= '9' || paramChar == ',' || paramChar == '+' || paramChar == '*' || paramChar == '#'
    }
}

