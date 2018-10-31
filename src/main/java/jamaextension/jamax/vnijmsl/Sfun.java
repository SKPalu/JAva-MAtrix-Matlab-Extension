/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamaextension.jamax.vnijmsl;

/**
 * 
 * @author Feynman Perceptrons
 */
public class Sfun
{

    // private static final int check = Messages.check(1);
    public static final double EPSILON_SMALL = 1.1102230246251999E-016D;
    public static final double EPSILON_LARGE = 2.2204460492503E-016D;
    private static final double COT_COEF[] =
    {
            0.2402591609829563D, -0.016533031601500228D, -4.2998391931724018E-005D, -1.5928322332754105E-007D,
            -6.1910931351293491E-010D, -2.430197415072646E-012D, -9.5609367588000803E-015D, -3.7635379819458057E-017D,
            -1.4816657464674657E-019D
    };
    private static final double GAMMA_COEF[] =
    {
            0.0085711955909893306D, 0.0044153813248410069D, 0.056850436815993631D, -0.0042198353964185602D,
            0.0013268081812124603D, -0.00018930245297988805D, 3.6069253274412453E-005D, -6.0567619044608639E-006D,
            1.0558295463022833E-006D, -1.8119673655423841E-007D, 3.1177249647153221E-008D, -5.3542196390196869E-009D,
            9.1932755198595892E-010D, -1.5779412802883398E-010D, 2.7079806229349544E-011D, -4.6468186538257299E-012D,
            7.9733501920074201E-013D, -1.368078209830916E-013D, 2.3473194865638007E-014D, -4.0274326149490668E-015D,
            6.9100517473721006E-016D, -1.1855845002219929E-016D, 2.0341485424963741E-017D, -3.4900543417174062E-018D,
            5.9879938564853056E-019D, -1.027378057872228E-019D
    };
    private static final double R9LGMC_COEF[] =
    {
            0.16663894804518634D, -1.3849481760675639E-005D, 9.8108256469247302E-009D, -1.8091294755724941E-011D,
            6.2210980418926055E-014D, -3.399615005417722E-016D, 2.6831819984826989E-018D
    };
    private static final double ALNRCS_COEF[] =
    {
            1.037869356274377D, -0.13364301504908918D, 0.019408249135520562D, -0.0030107551127535777D,
            0.00048694614797154852D, -8.1054881893175362E-005D, 1.3778847799559525E-005D, -2.380221089435897E-006D,
            4.1640416213865184E-007D, -7.3595828378075992E-008D, 1.3117611876241675E-008D, -2.3546709317742423E-009D,
            4.2522773276035E-010D, -7.7190894134840799E-011D, 1.407574648135907E-011D, -2.5769072058024682E-012D,
            4.7342406666294419E-013D, -8.7249012674742641E-014D, 1.6124614902740551E-014D, -2.9875652015665774E-015D,
            5.5480701209082887E-016D, -1.0324619158271569E-016D, 1.9250239203049852E-017D, -3.5955073465265147E-018D,
            6.726454253787686E-019D, -1.260262416873522E-019D
    };
    private static final double ERFC_COEF[] =
    {
            -0.049046121234691806D, -0.14226120510371365D, 0.010035582187599796D, -0.00057687646997674853D,
            2.741993125219606E-005D, -1.1043175507344507E-006D, 3.8488755420345036E-008D, -1.1808582533875466E-009D,
            3.2334215826050907E-011D, -7.9910159470045487E-013D, 1.7990725113961456E-014D, -3.7186354878186928E-016D,
            7.1035990037142532E-018D, -1.2612455119155226E-019D
    };
    private static final double ERFC2_COEF[] =
    {
            -0.069601346602309502D, -0.041101339362620892D, 0.0039144958666896268D, -0.00049063956505489791D,
            7.1574790013770361E-005D, -1.1530716341312328E-005D, 1.9946705902019974E-006D, -3.6426664715992229E-007D,
            6.9443726100050124E-008D, -1.3712209021043659E-008D, 2.7883896610071373E-009D, -5.8141647243311614E-010D,
            1.2389204917527532E-010D, -2.6906391453067435E-011D, 5.9426143508479114E-012D, -1.3323867357581197E-012D,
            3.0280468061771323E-013D, -6.9666488149410327E-014D, 1.620854541053923E-014D, -3.8099344652504917E-015D,
            9.0404878159788311E-016D, -2.1640061950896072E-016D, 5.2221022339958551E-017D, -1.2697296023645554E-017D,
            3.1091455042761977E-018D, -7.6637629203203857E-019D, 1.9008192513627452E-019D
    };
    private static final double ERFCC_COEF[] =
    {
            0.071517931020292483D, -0.026532434337606717D, 0.0017111539779208558D, -0.00016375166345851787D,
            1.9871293500552038E-005D, -2.8437124127665552E-006D, 4.6061613089631305E-007D, -8.227753025879209E-008D,
            1.5921418727709012E-008D, -3.2950713622528431E-009D, 7.2234397604005558E-010D, -1.6648558133987297E-010D,
            4.0103925882376649E-011D, -1.0048162144257311E-011D, 2.6082759133003339E-012D, -6.9911105604040245E-013D,
            1.9294923332617072E-013D, -5.4701311887543309E-014D, 1.5896633097626975E-014D, -4.7268939801975551E-015D,
            1.4358733767849847E-015D, -4.4495105618173579E-016D, 1.4048108847682335E-016D, -4.5138183877642106E-017D,
            1.4745215410451331E-017D, -4.8926214069457765E-018D, 1.6476121414106467E-018D, -5.6268171763294081E-019D,
            1.9474433822320786E-019D
    };
    private static final double ERFI_COEF[] =
    {
            -0.05102816076058251D, 0.097671213654297623D, 0.0106798426748877D, 0.0014583926193423599D,
            0.00022139561793534646D, 3.5754642695386944E-005D, 6.0130524358465269E-006D, 1.0405605281919531E-006D,
            1.8393099988652242E-007D, 3.3048595802566894E-008D, 6.0159229649242211E-009D, 1.1067505460742694E-009D,
            2.0540500062873634E-010D, 3.8404854958353785E-011D, 7.2261351430130507E-012D, 1.3670906380006421E-012D,
            2.598708098538055E-013D, 4.9606497115239372E-014D, 9.5046123635890536E-015D, 1.8271407438754928E-015D,
            3.5229350975383005E-016D, 6.8109374824400253E-017D, 1.319990081950303E-017D, 2.5639064134405433E-018D,
            4.9902175821710614E-019D, 9.7308471941199002E-020D, 1.9007812300464191E-020D, 3.7188403192614501E-021D,
            7.2866273739280995E-022D, 1.4296976677763E-022D, 2.8087948002380001E-023D, 5.5248115832999998E-024D,
            1.08793649641E-024D, 2.1446095381000001E-025D, 4.2317909600000001E-026D, 8.3580796799999993E-027D,
            1.6522367500000001E-027D, 3.2688917000000002E-028D, 6.4724989999999996E-029D, 1.282534E-029D,
            2.54316E-030D, 5.0462999999999998E-031D, 1.0020000000000001E-031D, 1.9909999999999999E-032D
    };
    private static final double ERFCI_COEF2[] =
    {
            0.43841585477804562D, 0.15496564285206799D, 0.024348970850171091D, 0.0049393503168608044D,
            0.0011275848754368871D, 0.00027535743525411227D, 7.0224672857948067E-005D, 1.8458899973617858E-005D,
            4.9610728835315062E-006D, 1.3562518494282876E-006D, 3.7579302019082853E-007D, 1.0526644220157781E-007D,
            2.9753753415489186E-008D, 8.4738069637299847E-009D, 2.4289214516182432E-009D, 7.0010011476225411E-010D,
            2.0277152716342504E-010D, 5.8979208174014096E-011D, 1.7219667036776894E-011D, 5.0443751878018648E-012D,
            1.4821666957231796E-012D, 4.3668262365500944E-013D, 1.2897453453497589E-013D, 3.8178240801302582E-014D,
            1.1324488927217862E-014D, 3.3654188832404761E-015D, 1.0018740212489858E-015D, 2.9873322472683708E-016D,
            8.920712569016611E-017D, 2.6675693344692858E-017D, 7.9871384096104718E-018D, 2.3943587708627562E-018D,
            7.1858222569356769E-019D, 2.1588513175543796E-019D, 6.4923288800325485E-020D, 1.9542706135327389E-020D,
            5.8877901668170599E-021D, 1.7753430834603299E-021D, 5.3574140436077E-022D, 1.6179056840626E-022D,
            4.8894469640269999E-023D, 1.478630074017E-023D, 4.4744370429499999E-024D, 1.3548248630199999E-024D,
            4.1047010578999999E-025D, 1.2442913625E-025D, 3.773932534E-026D, 1.14521671E-026D,
            3.4769092700000003E-027D, 1.05609356E-027D, 3.2092721E-028D, 9.7565900000000003E-029D,
            2.9673560000000001E-029D, 9.0284700000000003E-030D, 2.7480599999999999E-030D, 8.3675000000000005E-031D,
            2.5487E-031D, 7.766E-032D, 2.367E-032D
    };
    private static final double SQRTPI = 1.7724538509055161D;
    private static final double alneps = -Math.log(1.1102230246251999E-016D);
    private static final double sqeps = Math.sqrt(2.2204460492503E-016D);
    private static final double bot = Math.log(4.9406564584124654E-322D);

    private Sfun()
    {
    }

    static double csevl(double d, double ad[])
    {
        double d2 = 0.0D;
        double d1 = 0.0D;
        double d3 = 0.0D;
        double d4 = 2D * d;
        for (int i = ad.length - 1; i >= 0; i--)
        {
            d3 = d2;
            d2 = d1;
            d1 = (d4 * d2 - d3) + ad[i];
        }

        return 0.5D * (d1 - d3);
    }

    public static double cot(double d)
    {
        double d7 = 0.011619772367581343D;
        double d5 = Math.abs(d);
        if (d5 > 4503600000000000D)
        {
            return (0.0D / 0.0D);
        }
        double d2 = (int) d5;
        double d6 = d5 - d2;
        double d4 = 0.625D * d2;
        d2 = (int) d4;
        d5 = (d4 - d2) + 0.625D * d6 + d5 * d7;
        double d3 = (int) d5;
        d2 += d3;
        d5 -= d3;
        int i = (int) (d2 % 2D);
        if (i == 1)
        {
            d5 = 1.0D - d5;
        }
        double d1;
        if (d5 == 0.0D)
        {
            d1 = (1.0D / 0.0D);
        }
        else if (d5 <= 1.82501E-008D)
        {
            d1 = 1.0D / d5;
        }
        else if (d5 <= 0.25D)
        {
            d1 = (0.5D + csevl(32D * d5 * d5 - 1.0D, COT_COEF)) / d5;
        }
        else if (d5 <= 0.5D)
        {
            d1 = (0.5D + csevl(8D * d5 * d5 - 1.0D, COT_COEF)) / (0.5D * d5);
            d1 = ((d1 * d1 - 1.0D) * 0.5D) / d1;
        }
        else
        {
            d1 = (0.5D + csevl(2D * d5 * d5 - 1.0D, COT_COEF)) / (0.25D * d5);
            d1 = ((d1 * d1 - 1.0D) * 0.5D) / d1;
            d1 = ((d1 * d1 - 1.0D) * 0.5D) / d1;
        }
        if (d != 0.0D)
        {
            d1 = sign(d1, d);
        }
        if (i == 1)
        {
            d1 = -d1;
        }
        return d1;
    }

    public static double log10(double d)
    {
        return 0.43429448190325182D * Math.log(d);
    }

    public static double sign(double d, double d1)
    {
        double d2 = d >= 0.0D ? d : -d;
        return d1 >= 0.0D ? d2 : -d2;
    }

    public static double fact(int i)
    {
        double d = 1.0D;
        if (i < 0)
        {
            d = (0.0D / 0.0D);
        }
        else if (i > 170)
        {
            d = (1.0D / 0.0D);
        }
        else
        {
            for (int j = 2; j <= i; j++)
            {
                d *= j;
            }

        }
        return d;
    }

    public static double gamma(double d)
    {
        double d2 = Math.abs(d);
        double d1;
        if (d2 <= 10D)
        {
            int i = (int) d;
            if (d < 0.0D)
            {
                i--;
            }
            d2 = d - (double) i;
            i--;
            d1 = 0.9375D + csevl(2D * d2 - 1.0D, GAMMA_COEF);
            if (i != 0)
            {
                if (i < 0)
                {
                    i = -i;
                    if (d == 0.0D)
                    {
                        d1 = (0.0D / 0.0D);
                    }
                    else if (d2 < 5.5626846462680035E-309D)
                    {
                        d1 = (1.0D / 0.0D);
                    }
                    else
                    {
                        double d4 = i - 2;
                        if (d < 0.0D && d + d4 == 0.0D)
                        {
                            d1 = (0.0D / 0.0D);
                        }
                        else
                        {
                            for (int k = 0; k < i; k++)
                            {
                                d1 /= d + (double) k;
                            }

                        }
                    }
                }
                else
                {
                    for (int j = 1; j <= i; j++)
                    {
                        d1 *= d2 + (double) j;
                    }

                }
            }
        }
        else if (d > 171.614D)
        {
            d1 = (1.0D / 0.0D);
        }
        else if (d < -170.56D)
        {
            d1 = 0.0D;
        }
        else
        {
            d1 = Math.exp(((d2 - 0.5D) * Math.log(d2) - d2) + 0.91893853320467267D + r9lgmc(d2));
            if (d < 0.0D)
            {
                double d3 = Math.sin(3.1415926535897931D * d2);
                if (d3 == 0.0D || (double) Math.round(d2) == d2)
                {
                    d1 = (0.0D / 0.0D);
                }
                else
                {
                    d1 = -3.1415926535897931D / (d2 * d3 * d1);
                }
            }
        }
        return d1;
    }

    public static double logGamma(double d)
    {
        double d3 = Math.abs(d);
        double d1;
        if (d3 <= 10D)
        {
            d1 = Math.log(Math.abs(gamma(d)));
        }
        else if (d > 0.0D)
        {
            d1 = ((0.91893853320467267D + (d - 0.5D) * Math.log(d)) - d) + r9lgmc(d3);
        }
        else
        {
            double d2 = Math.abs(Math.sin(3.1415926535897931D * d3));
            if (d2 == 0.0D || (double) Math.round(d3) == d3)
            {
                d1 = (0.0D / 0.0D);
            }
            else
            {
                d1 = (0.22579135264472744D + (d - 0.5D) * Math.log(d3)) - d - Math.log(d2) - r9lgmc(d3);
            }
        }
        return d1;
    }

    public static double r9lgmc(double d)
    {
        double d1;
        if (d < 10D)
        {
            d1 = (0.0D / 0.0D);
        }
        else if (d < 94906265.620000005D)
        {
            double d2 = 10D / d;
            d1 = csevl(2D * d2 * d2 - 1.0D, R9LGMC_COEF) / d;
        }
        else if (d < 139118000000D)
        {
            d1 = 1.0D / (12D * d);
        }
        else
        {
            d1 = 0.0D;
        }
        return d1;
    }

    public static double beta(double d, double d1)
    {
        if (d <= 0.0D || d1 <= 0.0D)
        {
            return (0.0D / 0.0D);
        }
        if (d + d1 < 171.614D)
        {
            return (gamma(d) * gamma(d1)) / gamma(d + d1);
        }
        else
        {
            return Math.exp(logBeta(d, d1));
        }
    }

    public static double logBeta(double d, double d1)
    {
        double d5 = Math.min(d, d1);
        double d6 = Math.max(d, d1);
        double d4;
        if (d5 <= 0.0D)
        {
            d4 = (0.0D / 0.0D);
        }
        else if (d5 >= 10D)
        {
            double d2 = (r9lgmc(d5) + r9lgmc(d6)) - r9lgmc(d5 + d6);
            double d7 = dlnrel(-d5 / (d5 + d6));
            d4 = -0.5D * Math.log(d6) + 0.91893853320467278D + d2 + (d5 - 0.5D) * Math.log(d5 / (d5 + d6)) + d6 * d7;
        }
        else if (d6 >= 10D)
        {
            double d3 = r9lgmc(d6) - r9lgmc(d5 + d6);
            d4 = ((logGamma(d5) + d3 + d5) - d5 * Math.log(d5 + d6)) + (d6 - 0.5D) * dlnrel(-d5 / (d5 + d6));
        }
        else
        {
            d4 = Math.log(gamma(d5) * (gamma(d6) / gamma(d5 + d6)));
        }
        return d4;
    }

    public static double betaIncomplete(double d, double d1, double d2)
    {
        double d14 = d1;
        if (d < 0.0D || d > 1.0D)
        {
            return (0.0D / 0.0D);
        }
        if (d1 <= 0.0D || d2 <= 0.0D)
        {
            return (0.0D / 0.0D);
        }
        double d13 = d;
        if ((d2 > d1 || d >= 0.80000000000000004D) && d >= 0.20000000000000001D)
        {
            d13 = 1.0D - d13;
            double d15 = d1;
            d1 = d2;
            d2 = d15;
        }
        if (((d1 + d2) * d13) / (d1 + 1.0D) < 1.1102230246251999E-016D)
        {
            double d3 = 0.0D;
            double d11 = d1 * Math.log(Math.max(d13, 4.9406564584124654E-324D)) - Math.log(d1) - logBeta(d1, d2);
            if (d11 > bot && d13 != 0.0D)
            {
                d3 = Math.exp(d11);
            }
            if (d13 != d || d1 != d14)
            {
                d3 = 1.0D - d3;
            }
            return d3;
        }
        double d8 = d2 - (double) (int) d2;
        if (d8 == 0.0D)
        {
            d8 = 1.0D;
        }
        double d12 = d1 * Math.log(d13) - logBeta(d8, d1) - Math.log(d1);
        double d4 = 0.0D;
        if (d12 >= bot)
        {
            d4 = Math.exp(d12);
            double d9 = d4 * d1;
            if (d8 != 1.0D)
            {
                int l = (int) Math.max(-alneps / Math.log(d13), 4D);
                for (int i = 1; i <= l; i++)
                {
                    d9 = (d9 * ((double) i - d8) * d13) / (double) i;
                    d4 += d9 / (d1 + (double) i);
                }

            }
        }
        if (d2 <= 1.0D)
        {
            if (d13 != d || d1 != d14)
            {
                d4 = 1.0D - d4;
            }
            return Math.max(Math.min(d4, 1.0D), 0.0D);
        }
        d12 = (d1 * Math.log(d13) + d2 * Math.log(1.0D - d13)) - logBeta(d1, d2) - Math.log(d2);
        int k = (int) Math.max(d12 / bot, 0.0D);
        double d10 = Math.exp(d12 - (double) k * bot);
        double d5 = 1.0D / (1.0D - d13);
        double d7 = (d2 * d5) / ((d1 + d2) - 1.0D);
        double d6 = 0.0D;
        int i1 = (int) d2;
        if (d2 == (double) i1)
        {
            i1--;
        }
        for (int j = 1; j <= i1 && (d7 > 1.0D || d10 / 1.1102230246251999E-016D > d6); j++)
        {
            d10 = ((d2 - (double) (j - 1)) * d5 * d10) / ((d1 + d2) - (double) j);
            if (d10 > 1.0D)
            {
                k--;
                d10 *= 4.9406564584124654E-324D;
            }
            if (k == 0)
            {
                d6 += d10;
            }
        }

        d4 += d6;
        if (d13 != d || d1 != d14)
        {
            d4 = 1.0D - d4;
        }
        return Math.max(Math.min(d4, 1.0D), 0.0D);
    }

    private static double logGammaSign(double d)
    {
        double d1 = 1.0D;
        if (d <= 0.0D)
        {
            int i = (int) d;
            int j = (int) ((double) i % 2D + 0.10000000000000001D);
            if (j == 0)
            {
                d1 = -1D;
            }
        }
        return d1;
    }

    public static double poch(double d, double d1)
    {
        double d2 = (0.0D / 0.0D);
        double d3 = d + d1;
        if (d3 <= 0.0D)
        {
            int i = (int) d3;
            if ((double) i == d3)
            {
                int j = (int) d;
                if (d > 0.0D || (double) j != d)
                {
                    return d2;
                }
                if (d1 == 0.0D)
                {
                    return 1.0D;
                }
                int l = (int) d1;
                if (Math.min(d + d1, d) >= -20D)
                {
                    int j1 = -(int) d;
                    d2 = (Math.pow(-1D, l) * fact(j1)) / fact(j1 - l);
                }
                else
                {
                    d2 = Math.pow(-1D, l)
                            * Math.exp(((((d - 0.5D) * Hyperbolic.log1p(d1 / (d - 1.0D)) + d1
                                    * Math.log((-d + 1.0D) - d1)) - d1) + r9lgmc(-d + 1.0D))
                                    - r9lgmc((-d - d1) + 1.0D));
                }
                return d2;
            }
        }
        d2 = 0.0D;
        int k = (int) d;
        if (d <= 0.0D && (double) k == d)
        {
            return d2;
        }
        int i1 = (int) Math.abs(d1);
        if ((double) i1 == d1 && i1 <= 20)
        {
            d2 = 1.0D;
            for (int k1 = 0; k1 < i1; k1++)
            {
                d2 *= d + (double) k1;
            }

            return d2;
        }
        double d4 = Math.abs(d + d1);
        double d5 = Math.abs(d);
        if (Math.max(d4, d5) <= 20D)
        {
            return gamma(d + d1) / gamma(d);
        }
        if (Math.abs(d1) > 0.5D * d5)
        {
            double d6 = logGamma(d + d1);
            double d8 = logGamma(d);
            d2 = logGammaSign(d + d1) * logGammaSign(d) * Math.exp(d6 - d8);
        }
        double d7 = d;
        if (d7 < 0.0D)
        {
            d7 = (-d - d1) + 1.0D;
        }
        d2 = ((((d7 - 0.5D) * Hyperbolic.log1p(d1 / d7) + d1 * Math.log(d7 + d1)) - d1) + r9lgmc(d7 + d1)) - r9lgmc(d7);
        d2 = Math.exp(d2);
        if (d >= 0.0D || d2 == 0.0D)
        {
            return d2;
        }
        double d9 = Math.cos(3.1415926535897931D * d1);
        double d10 = Math.sin(3.1415926535897931D * d1);
        double d11 = Math.cos(3.1415926535897931D * d);
        double d12 = Math.sin(3.1415926535897931D * d);
        double d13 = Math.abs(d1) * (1.0D + Math.log(d7));
        double d14 = d9 + (d11 * d10) / d12;
        double d15 = (Math.abs(d1) * (Math.abs(d10) + Math.abs((d11 * d9) / d12)) + Math.abs(d * d10)
                / Math.pow(d12, 2D)) * 3.1415926535897931D;
        d15 = d13 + d15 / Math.abs(d14);
        if (d15 > 4503599627370523D)
        {
            return (0.0D / 0.0D);
        }
        else
        {
            d2 /= d14;
            return d2;
        }
    }

    private static double dlnrel(double d)
    {
        double d1;
        if (d <= -1D)
        {
            d1 = (0.0D / 0.0D);
        }
        else if (Math.abs(d) <= 0.375D)
        {
            d1 = d * (1.0D - d * csevl(d / 0.375D, ALNRCS_COEF));
        }
        else
        {
            d1 = Math.log(1.0D + d);
        }
        return d1;
    }

    public static double erf(double d)
    {
        double d2 = Math.abs(d);
        double d1;
        if (d2 <= 1.49012E-008D)
        {
            d1 = (2D * d) / 1.7724538509055161D;
        }
        else if (d2 <= 1.0D)
        {
            d1 = d * (1.0D + csevl(2D * d * d - 1.0D, ERFC_COEF));
        }
        else if (d2 < 6.0136873570000002D)
        {
            d1 = sign(1.0D - erfc(d2), d);
        }
        else
        {
            d1 = sign(1.0D, d);
        }
        return d1;
    }

    public static double erfc(double d)
    {
        double d2 = Math.abs(d);
        double d1;
        if (d <= -6.0136873570000002D)
        {
            d1 = 2D;
        }
        else if (d2 < 1.49012E-008D)
        {
            d1 = 1.0D - (2D * d) / 1.7724538509055161D;
        }
        else
        {
            double d3 = d2 * d2;
            if (d2 < 1.0D)
            {
                d1 = 1.0D - d * (1.0D + csevl(2D * d3 - 1.0D, ERFC_COEF));
            }
            else if (d2 <= 4D)
            {
                d1 = (Math.exp(-d3) / d2) * (0.5D + csevl((8D / d3 - 5D) / 3D, ERFC2_COEF));
                if (d < 0.0D)
                {
                    d1 = 2D - d1;
                }
                if (d < 0.0D)
                {
                    d1 = 2D - d1;
                }
                if (d < 0.0D)
                {
                    d1 = 2D - d1;
                }
            }
            else
            {
                d1 = (Math.exp(-d3) / d2) * (0.5D + csevl(8D / d3 - 1.0D, ERFCC_COEF));
                if (d < 0.0D)
                {
                    d1 = 2D - d1;
                }
            }
        }
        return d1;
    }

    public static double erfInverse(double d)
    {
        double d1 = Math.abs(d);
        if (d == 1.0D)
        {
            return (1.0D / 0.0D);
        }
        if (d == -1D)
        {
            return (-1.0D / 0.0D);
        }
        if (d1 > 1.0D)
        {
            return (0.0D / 0.0D);
        }
        if (d1 <= 0.75D)
        {
            double d2 = 0.0D;
            if (d1 > 1.0536712127723714E-008D)
            {
                d2 = d * d;
            }
            return d * (1.0D + csevl((32D * d2) / 9D - 1.0D, ERFI_COEF));
        }
        if (d1 <= 0.9375D)
        {
            return d * (1.0D + csevl((512D * d * d - 369D) / 81D, ERFCI_COEF2));
        }
        else
        {
            return sign(erfcInverse(1.0D - d1), d);
        }
    }

    public static double erfcInverse(double d)
    {
        if (d == 0.0D)
        {
            return (1.0D / 0.0D);
        }
        if (d == 2D)
        {
            return (-1.0D / 0.0D);
        }
        if (d < 0.0D || d > 2D)
        {
            return (0.0D / 0.0D);
        }
        double d1 = 1.0D - d;
        double d2 = Math.abs(d1);
        if (d2 >= 0.75D)
        {
            double d3 = d1 < 0.0D ? 1.0D + (1.0D - d) : d;
            double d4 = Math.log(d3);
            double d5 = -Math.log(1.7724538509055161D * d3);
            double d6 = Math.log(d5);
            double d7 = Math.sqrt((d5 - 0.5D * d6) + (0.25D * d6 - 0.5D) / d5);
            boolean flag = false;
            int i = 1;
            do
            {
                if (i > 100)
                {
                    break;
                }
                double d8 = 0.88622692545275805D * (erfce(d7) - Math.exp(d4 + d7 * d7));
                d7 += d8;
                if (Math.abs(d8) < 2.2204460492503001E-015D * d7)
                {
                    flag = true;
                    break;
                }
                i++;
            }
            while (true);
            if (!flag)
            {
                return (0.0D / 0.0D);
            }
            if (d1 < 0.0D)
            {
                d7 = -d7;
            }
            return d7;
        }
        else
        {
            return d1 * (1.0D + csevl((32D * d1 * d1) / 9D - 1.0D, ERFI_COEF));
        }
    }

    private static double erfce(double d)
    {
        if (d < -26.618735713751487D)
        {
            return (1.0D / 0.0D);
        }
        double d1;
        if (d <= -6.0136873569177478D)
        {
            d1 = 2D * Math.exp(d * d);
        }
        else if (d <= 1.7798057893024306E+308D)
        {
            double d2 = Math.abs(d);
            if (d2 <= 1.0D)
            {
                if (d2 < 1.4901161193847947E-008D)
                {
                    d1 = 1.0D - (2D * d) / 1.7724538509055161D;
                }
                else
                {
                    d1 = Math.exp(d * d) * (1.0D - d * (1.0D + csevl(2D * d * d - 1.0D, ERFC_COEF)));
                }
            }
            else
            {
                d2 *= d2;
                if (d2 <= 4D)
                {
                    d1 = (0.5D + csevl((8D / d2 - 5D) / 3D, ERFC2_COEF)) / Math.abs(d);
                }
                else
                {
                    d1 = (0.5D + csevl(8D / d2 - 1.0D, ERFCC_COEF)) / Math.abs(d);
                }
                if (d < 0.0D)
                {
                    d1 = 2D * Math.exp(d2) - d1;
                }
            }
        }
        else
        {
            d1 = 0.0D;
        }
        return d1;
    }

    private static double gammaIncomplete(double d, double d1)
    {
        double d2 = 0.0D;
        double d4 = 0.0D;
        if (d1 < 0.0D)
        {
            return (0.0D / 0.0D);
        }
        double d7 = d1 == 0.0D ? 0.0D : Math.log(d1);
        double d8 = 1.0D;
        if (d != 0.0D)
        {
            d8 = sign(1.0D, d);
        }
        double d9 = (int) (d + 0.5D * d8);
        double d10 = d - d9;
        if (d1 <= 0.0D)
        {
            double d6 = 0.0D;
            if (d9 > 0.0D || d10 != 0.0D)
            {
                d6 = gammaReciprocal(d + 1.0D);
            }
            return d6;
        }
        if (d1 <= 1.0D)
        {
            if (d >= -0.5D || d10 != 0.0D)
            {
                d2 = logGamma(d + 1.0D);
                d4 = logGammaSign(d + 1.0D);
            }
            return incGammaTricomi(d, d1, d2, d4);
        }
        if (d1 <= d)
        {
            return Math.exp(logGammaTricomi(d, d1, logGamma(d + 1.0D)));
        }
        double d11 = logCompIncGamma(d, d1, d7);
        double d12 = 1.0D;
        if (d10 != 0.0D || d9 > 0.0D)
        {
            double d3 = logGamma(d + 1.0D);
            double d5 = logGammaSign(d + 1.0D);
            double d13 = (Math.log(Math.abs(d)) + d11) - d3;
            if (d13 > alneps)
            {
                d13 += -d * d7;
                return -d8 * d5 * Math.exp(d13);
            }
            if (d13 > -alneps)
            {
                d12 = 1.0D - d8 * d5 * Math.exp(d13);
            }
        }
        return sign(Math.exp(-d * d7 + Math.log(Math.abs(d12))), d12);
    }

    private static double incGammaTricomi(double d, double d1, double d2, double d3)
    {
        double d4 = 0.0D;
        double d5 = 0.0D;
        double d6 = (0.0D / 0.0D);
        if (d4 == 0.0D)
        {
            d4 = 5.5511151231259996E-017D;
            d5 = Math.log(4.9406564584124654E-324D);
        }
        if (d1 <= 0.0D)
        {
            return (0.0D / 0.0D);
        }
        int i = (int) (d < 0.0D ? d - 0.5D : d + 0.5D);
        double d7 = d - (double) i;
        double d8 = d < -0.5D ? d7 : d;
        double d9 = 1.0D;
        double d10 = d8;
        double d11 = d9;
        boolean flag = false;
        double d12 = 1;
        do
        {
            if (d12 > 200)
            {
                break;
            }
            double d13 = d12;
            d10 = (-d1 * d10) / d13;
            d9 = d10 / (d8 + d13);
            d11 += d9;
            if (Math.abs(d9) < d4 * Math.abs(d11))
            {
                flag = true;
                break;
            }
            d12++;
        }
        while (true);
        if (!flag)
        {
            return (0.0D / 0.0D);
        }
        if (d >= -0.5D)
        {
            return Math.exp(-d2 + Math.log(d11));
        }
        d12 = -logGamma(1.0D + d7) + Math.log(d11);
        d11 = 1.0D;
        int j = -i - 1;
        d9 = 1.0D;
        double d14 = 1;
        do
        {
            if (d14 > j)
            {
                break;
            }
            d9 = (d1 * d9) / (d7 - (double) ((j + 1) - d14));
            d11 += d9;
            if (Math.abs(d9) < d4 * Math.abs(d11))
            {
                break;
            }
            d14++;
        }
        while (true);
        d6 = 0.0D;
        d12 += -(double) i * Math.log(d1);
        if (d11 == 0.0D || d7 == 0.0D)
        {
            return Math.exp(d12);
        }
        d14 = d3 * sign(1.0D, d11);
        double d15 = (-d1 - d2) + Math.log(Math.abs(d11));
        if (d15 > d5)
        {
            d6 = d14 * Math.exp(d15);
        }
        if (d12 > d5)
        {
            d6 += Math.exp(d12);
        }
        return d6;
    }

    private static double gammaReciprocal(double d)
    {
        if (d <= 0.0D && (double) (int) d == d)
        {
            return 0.0D;
        }
        if (Math.abs(d) <= 10D)
        {
            return 1.0D / gamma(d);
        }
        else
        {
            return logGammaSign(d) * Math.exp(logGamma(d));
        }
    }

    private static double logGammaTricomi(double d, double d1, double d2)
    {
        if (d1 <= 0.0D || d < d1)
        {
            return (0.0D / 0.0D);
        }
        double d3 = d + d1;
        double d4 = d3 + 1.0D;
        double d5 = 0.0D;
        double d6 = 1.0D;
        double d7 = d6;
        boolean flag = false;
        int i = 1;
        do
        {
            if (i > 200)
            {
                break;
            }
            double d9 = i;
            double d10 = (d + d9) * d1 * (1.0D + d5);
            d5 = d10 / ((d3 + d9) * (d4 + d9) - d10);
            d6 *= d5;
            d7 += d6;
            if (Math.abs(d6) < 5.5511151231259996E-017D * d7)
            {
                flag = true;
                break;
            }
            i++;
        }
        while (true);
        if (!flag)
        {
            return (0.0D / 0.0D);
        }
        else
        {
            double d8 = 1.0D - (d1 * d7) / d4;
            return -d1 - d2 - Math.log(d8);
        }
    }

    private static double logCompIncGamma(double d, double d1, double d2)
    {
        double d3 = (d1 + 1.0D) - d;
        double d4 = d1 - 1.0D - d;
        double d5 = 0.0D;
        double d6 = 1.0D;
        double d7 = d6;
        boolean flag = false;
        int i = 1;
        do
        {
            if (i > 200)
            {
                break;
            }
            double d8 = i;
            double d9 = d8 * (d - d8) * (1.0D + d5);
            d5 = -d9 / ((d4 + 2D * d8) * (d3 + 2D * d8) + d9);
            d6 *= d5;
            d7 += d6;
            if (Math.abs(d6) < 5.5511151231259996E-017D * d7)
            {
                flag = true;
                break;
            }
            i++;
        }
        while (true);
        if (!flag)
        {
            return (0.0D / 0.0D);
        }
        else
        {
            return (d * d2 - d1) + Math.log(d7 / d3);
        }
    }
}
