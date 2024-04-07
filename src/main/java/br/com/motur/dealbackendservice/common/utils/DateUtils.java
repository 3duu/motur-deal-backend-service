package br.com.motur.dealbackendservice.common.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateUtils  {

    public static final  List<Integer> DIAS_VENCIMENTO_POSSIVEIS;
    public static final  List<Integer> DIAS_VENCIMENTO_DISPONIVEIS;

    public static int ANO_ATUAL;
    public static int MES_ATUAL;
    private static Set<Date> feriados;

    static {
        List<Integer> temp = new ArrayList<>();
        temp.add(5);
        DIAS_VENCIMENTO_DISPONIVEIS = Collections.unmodifiableList(temp);
        temp = new ArrayList<>();
        temp.add(5);
        temp.add(12);
        temp.add(20);
        DIAS_VENCIMENTO_POSSIVEIS = Collections.unmodifiableList(temp);
    }

    public static Date getProximoDiaVencimento(){
        return getProximoDiaVencimento(new Date());
    }

    public static Date getUltimoDiaVencimento(){
        return getUltimoDiaVencimento(new Date());
    }

    public static Integer getDiaVencimentoMaisProximo(){
        return getDiaVencimentoMaisProximo(new Date());
    }

    public static Integer getDiaVencimentoMaisProximo(Date base){
        Date dataMaisProxima = getProximoDiaVencimento(base);
        Calendar cal = new GregorianCalendar();
        cal.setTime(dataMaisProxima);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    public static Integer getDiaVencimentoAnterior(){
        Date dataUltimoDia = getUltimoDiaVencimento(new Date());
        Calendar cal = new GregorianCalendar();
        cal.setTime(dataUltimoDia);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    public static boolean isDiaVencimento(Integer dia) {
        for(Integer diap : DIAS_VENCIMENTO_POSSIVEIS){
            if(dia.equals(diap))
                return true;
        }
        return false;
    }

    /**
     * Retorna o dia de vencimento dentro do mês atual com a seguinte regra:
     *      caso o dia seja igual o menor que 19 devemos deixar que o vencimento seja dia 5
     *      caso o dia seja maior que 19 então devemos deixar o vencimento para o dia 20
     * @return Integer 5 ou 20
     */
    public static Integer getDiaVencimentoNoMes(Date dataBase){
        Calendar cal = Calendar.getInstance();
        if (dataBase != null){
            cal.setTime(dataBase);
        }
        return getDia(getUltimoDiaVencimento(cal.getTime()));
    }

    /**
     * Retorna o dia de vencimento mais próximo da data atual.
     * @return
     */
    private static Date getProximoDiaVencimento(Date aPartirDe){
        Date dataMaisProxima = null;
        for(Integer dia : DIAS_VENCIMENTO_POSSIVEIS){
            Date proximaOcorrencia = getProximaOcorrenciaDia(aPartirDe, dia);

            if(dataMaisProxima == null
                    || proximaOcorrencia.getTime() < dataMaisProxima.getTime()){
                dataMaisProxima = proximaOcorrencia;
            }
        }

        return dataMaisProxima;
    }

    /**
     * Retorna o dia de vencimento mais próximo da data atual.
     * @return
     */
    private static Date getUltimoDiaVencimento(Date aAntesDe){
        Date dataUltimoVencimento = null;
        for(Integer dia : DIAS_VENCIMENTO_POSSIVEIS){
            Date ultimaOcorrencia = getOcorrenciaAnteriorDia(aAntesDe, dia);

            if(dataUltimoVencimento == null
                    || ultimaOcorrencia.getTime() > dataUltimoVencimento.getTime()){
                dataUltimoVencimento = ultimaOcorrencia;
            }
        }

        return dataUltimoVencimento;
    }

    public static List<Date> getDiasUteisDentroDoMesAPartirDe(Date diaAtual) {
        List<Date> diasUteisList = new ArrayList<>();
        Date mesAtual = new Date(diaAtual.getTime());
        do {
            if (isDiaUtil(diaAtual)) {
                diasUteisList.add(diaAtual);
            }

            diaAtual = getDataNDiasAFrente(diaAtual, 1);
        } while (diaAtual != null && isSameMonth(diaAtual, mesAtual));

        return diasUteisList;
    }

    public static Date getDataFinal(final Date dataComparacao){
        return getDataNDiasAFrente(truncateDia(dataComparacao), 1);
    }

    public static Date getDataAtual() {
        Calendar cal = new GregorianCalendar();
        truncateDia((Calendar)cal);
        return cal.getTime();
    }

    public static int getDia(Date dia) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(dia != null ? dia : new Date());
        return cal.get(5);
    }

    public static int getDia() {
        return getDia((Date)null);
    }

    public static Date getPrimeiroDiaMesAtual() {
        return getPrimeiroDiaMes(new Date());
    }

    public static Date getPrimeiroDiaMes(Date mes) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(mes);
        cal.set(5, 1);
        truncateDia((Calendar)cal);
        return cal.getTime();
    }

    public static Date getUltimoDiaMesAtual() {
        return getUltimoDiaMes(new Date());
    }

    public static Date getUltimoDiaMes(Date mes) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(getPrimeiroDiaMes(mes));
        cal.add(2, 1);
        cal.add(5, -1);
        truncateDia((Calendar)cal);
        return cal.getTime();
    }

    public static Date getUltimoDiaMesAnterior() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        cal.add(2, -1);
        return getUltimoDiaMes(cal.getTime());
    }

    public static Date getPrimeiroDiaMesSeguinte() {
        return getPrimeiroDiaMesSeguinte(new Date());
    }

    public static Date getPrimeiroDiaMesSeguinte(Date date) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.set(5, 1);
        cal.add(2, 1);
        truncateDia((Calendar)cal);
        return cal.getTime();
    }

    public static Date getPrimeiroDiaMesAnterior() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        cal.set(5, 1);
        cal.add(2, -1);
        truncateDia((Calendar)cal);
        return cal.getTime();
    }

    public static Date truncateDateDiaAdicionaDia(Date data) {
        Calendar cal = truncateDiaRetornaCalendar(data);
        cal.add(5, 1);
        return cal.getTime();
    }

    public static Calendar truncateDiaRetornaCalendar(Date data) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(data);
        truncateDia((Calendar)cal);
        return cal;
    }

    public static Date truncateDia(Date data) {
        return truncateDiaRetornaCalendar(data).getTime();
    }

    public static void truncateDia(Calendar cal) {
        cal.set(11, 0);
        cal.set(12, 0);
        cal.set(13, 0);
        cal.set(14, 0);
    }

    public static Date getDataUmaSemanaAtras() {
        Calendar cal = new GregorianCalendar();
        cal.add(5, -7);
        truncateDia((Calendar)cal);
        return cal.getTime();
    }

    public static Date getDataUmMesAtras() {
        Calendar cal = new GregorianCalendar();
        cal.add(2, -1);
        truncateDia((Calendar)cal);
        return cal.getTime();
    }

    public static Date getDataUmMesAFrente(Date data) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(data);
        cal.add(2, 1);
        truncateDia((Calendar)cal);
        return cal.getTime();
    }

    public static Date getDataUmDiaAntes(Date data) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(data);
        cal.add(5, -1);
        truncateDia((Calendar)cal);
        return cal.getTime();
    }

    public static Date getDataNDiasAFrente(Date data, int n) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(data);
        cal.add(5, n);
        truncateDia((Calendar)cal);
        return cal.getTime();
    }

    public static Date getDataNMinutosAFrente(Date data, int n) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(data);
        cal.add(12, n);
        return cal.getTime();
    }

    public static Date getDataNDiasAFrente(Date data, int n, boolean apenasDiasUteis) {
        if (apenasDiasUteis && n != 0) {
            Calendar cal = new GregorianCalendar();
            cal.setTime(data);
            int i = 0;
            int step = n / Math.abs(n);

            while(i != n) {
                cal.add(5, step);
                if (isDiaUtil((Calendar)cal)) {
                    i += step;
                }
            }

            truncateDia((Calendar)cal);
            return cal.getTime();
        } else {
            return getDataNDiasAFrente(data, n);
        }
    }

    public static Date getDataNMesesAFrente(Date data, int meses) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(data);
        cal.add(2, meses);
        truncateDia((Calendar)cal);
        return cal.getTime();
    }

    public static Date getDataOntem() {
        Calendar cal = new GregorianCalendar();
        cal.add(5, -1);
        truncateDia((Calendar)cal);
        return cal.getTime();
    }

    public static Date getProximaOcorrenciaDia(Date dataBase, int dia) {
        Calendar cal = truncateDiaRetornaCalendar(dataBase);
        Date dataAtual = cal.getTime();
        cal.set(5, dia);
        if (cal.getTime().before(dataAtual)) {
            cal.add(2, 1);
        }

        return cal.getTime();
    }

    public static Date getOcorrenciaAnteriorDia(Date dataBase, int dia) {
        Calendar cal = truncateDiaRetornaCalendar(dataBase);
        Date dataAtual = cal.getTime();
        cal.set(5, dia);
        if (cal.getTime().after(dataAtual)) {
            cal.add(2, -1);
        }

        return cal.getTime();
    }

    public static Date getProximaOcorrenciaDia(int dia) {
        return getProximaOcorrenciaDia(new Date(), dia);
    }

    public static Date getProximaOcorrenciaDia(Date dia) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(dia);
        int diaEscolhido = cal.get(5);
        return getProximaOcorrenciaDia(diaEscolhido);
    }

    public static Date getNDiasUteisAPartirDe(Date dataReferencia, int contador) {
        if (dataReferencia != null && contador != 0) {
            Calendar cal = new GregorianCalendar();
            cal.setTime(truncateDia(dataReferencia));
            int i = Math.abs(contador);

            do {
                if (contador < 0) {
                    cal.add(5, -1);
                } else {
                    cal.add(5, 1);
                }

                if (isDiaUtil((Calendar)cal)) {
                    --i;
                }
            } while(i > 0);

            return cal.getTime();
        } else {
            return null;
        }
    }

    public static Date getDiaUtil(Date dia) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(dia != null ? dia : new Date());
        truncateDia((Calendar)cal);

        while(!isDiaUtil((Calendar)cal)) {
            cal.add(5, 1);
        }

        return cal.getTime();
    }

    public static Date getProximoDiaUtil(Date dia) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(dia != null ? dia : new Date());
        truncateDia((Calendar)cal);

        do {
            cal.add(5, 1);
        } while(!isDiaUtil((Calendar)cal));

        return cal.getTime();
    }

    public static Date getDiaUtilAnterior(Date dia) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(dia != null ? dia : new Date());

        do {
            cal.add(5, -1);
        } while(!isDiaUtil((Calendar)cal));

        return cal.getTime();
    }

    public static Date getDiaUtilAnterior() {
        return getDiaUtilAnterior((Date)null);
    }

    public static int getDiaDaSemana(Date dia) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(dia != null ? dia : new Date());
        return cal.get(7);
    }

    public static boolean isDiaUtil(Date data) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(data != null ? data : new Date());
        truncateDia((Calendar)cal);
        return isDiaUtil((Calendar)cal);
    }

    public static int getDiaDaSemana() {
        return getDiaDaSemana((Date)null);
    }

    public static Date getProximaOcorrenciaDia(Date dataBase, Date dia) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(dia);
        int diaEscolhido = cal.get(5);
        return getProximaOcorrenciaDia(dataBase, diaEscolhido);
    }

    public static int getDiferencaDias(Date dataInicial, Date dataFinal) {
        Calendar calInicial = truncateDiaRetornaCalendar(dataInicial);
        Calendar calFinal = truncateDiaRetornaCalendar(dataFinal);
        long timeInicial = calInicial.getTime().getTime();
        long timeFinal = calFinal.getTime().getTime();
        return Math.round((float)(timeFinal - timeInicial) / 8.64E7F);
    }

    public static int getDiferencaHoras(Date dataInicial, Date dataFinal) {
        Calendar calInicial = new GregorianCalendar();
        calInicial.setTime(dataInicial);
        Calendar calFinal = new GregorianCalendar();
        calFinal.setTime(dataFinal);
        long timeInicial = calInicial.getTime().getTime();
        long timeFinal = calFinal.getTime().getTime();
        return Math.round((float)(timeFinal - timeInicial) / 3600000.0F);
    }

    public static int getDiferencaMinutos(Date dataInicial, Date dataFinal) {
        Calendar calInicial = new GregorianCalendar();
        calInicial.setTime(dataInicial);
        Calendar calFinal = new GregorianCalendar();
        calFinal.setTime(dataFinal);
        long timeInicial = calInicial.getTime().getTime();
        long timeFinal = calFinal.getTime().getTime();
        return Math.round((float)(timeFinal - timeInicial) / 60000.0F);
    }

    public static int getDiferencaSegundos(Date dataInicial, Date dataFinal) {
        Calendar calInicial = new GregorianCalendar();
        calInicial.setTime(dataInicial);
        Calendar calFinal = new GregorianCalendar();
        calFinal.setTime(dataFinal);
        long timeInicial = calInicial.getTime().getTime();
        long timeFinal = calFinal.getTime().getTime();
        return Math.round((float)(timeFinal - timeInicial) / 1000.0F);
    }

    public static int getAnoAtual() {
        return ANO_ATUAL;
    }

    public static int getMesAtual() {
        return MES_ATUAL;
    }

    public static int getMesForDate(Date dia) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(dia != null ? dia : new Date());
        return cal.get(2);
    }

    public static int getMesForDate() {
        return getMesForDate((Date)null);
    }

    public static SimpleDateFormat getMMYYYYSdf() {
        return getSdf("MM/yyyy");
    }

    public static SimpleDateFormat getMMMMYYYYSdf() {
        return getSdf("MMMM/yyyy");
    }

    public static SimpleDateFormat getYYYYMMddSdf() {
        return getSdf("yyyy-MM-dd");
    }

    public static SimpleDateFormat getddMMYYYYSdf() {
        return getSdf("dd/MM/yyyy");
    }

    public static SimpleDateFormat getdMYYYYSdf() {
        return getSdf("M/d/yyyy HH:mm");
    }

    public static SimpleDateFormat getddMMSdf() {
        return getSdf("dd/MM");
    }

    public static SimpleDateFormat getddMMYYYYHHmmssSdf() {
        return getSdf("dd/MM/yyyy HH:mm:ss");
    }

    public static SimpleDateFormat getddMMYYYYHHmmSdf() {
        return getSdf("dd/MM/yyyy HH:mm");
    }

    public static SimpleDateFormat getyyyyMMddSdf() {
        return getSdf("yyyyMMdd");
    }

    public static SimpleDateFormat getddMMYYYYhmmssaaSdf() {
        return getSdf("MM/dd/yyyy h:mm:ss aa");
    }

    public static SimpleDateFormat getEEEEddMMMMhhmmSdf() {
        return getSdf("EEEE, dd MMMM, HH:mm");
    }

    public static boolean isSameMonth(Date date1, Date date2) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date1);
        int month1 = cal.get(2);
        int year1 = cal.get(1);
        cal.setTime(date2);
        int month2 = cal.get(2);
        int year2 = cal.get(1);
        return month1 == month2 && year1 == year2;
    }

    public static boolean isSameDate(Date date1, Date date2) {
        Calendar cal1 = new GregorianCalendar();
        Calendar cal2 = new GregorianCalendar();
        cal1.setTime(date1);
        cal1.set(10, 0);
        cal1.set(11, 0);
        cal1.set(12, 0);
        cal1.set(13, 0);
        cal1.set(14, 0);
        cal2.setTime(date2);
        cal2.set(10, 0);
        cal2.set(11, 0);
        cal2.set(12, 0);
        cal2.set(13, 0);
        cal2.set(14, 0);
        return cal1.equals(cal2);
    }

    public static int getIdadeEmMeses(Date dataInicial) {
        Calendar calInicial = new GregorianCalendar();
        calInicial.setTime(dataInicial);
        Calendar calHoje = new GregorianCalendar();

        int idadeEmMeses;
        for(idadeEmMeses = 0; calHoje.after(calInicial); ++idadeEmMeses) {
            calHoje.add(2, -1);
        }

        return idadeEmMeses;
    }

    public static String getIdade(Date dataInicial) {
        int meses = getIdadeEmMeses(dataInicial);
        int anos = meses / 12;
        meses %= 12;
        StringBuffer buf = new StringBuffer();
        if (anos == 1) {
            buf.append("1 ano ");
        } else if (anos > 1) {
            buf.append(anos);
            buf.append(" anos ");
        }

        if (anos > 0 && meses > 0) {
            buf.append("e ");
        }

        if (meses == 1) {
            buf.append("1 mês");
        } else if (meses > 1) {
            buf.append(meses);
            buf.append(" meses");
        }

        return buf.toString();
    }

    public static Integer getMesIntFromString(String mes) {
        if (mes.equalsIgnoreCase("janeiro")) {
            return 1;
        } else if (mes.equalsIgnoreCase("fevereiro")) {
            return 2;
        } else if (mes.equalsIgnoreCase("março")) {
            return 3;
        } else if (mes.equalsIgnoreCase("abril")) {
            return 4;
        } else if (mes.equalsIgnoreCase("maio")) {
            return 5;
        } else if (mes.equalsIgnoreCase("junho")) {
            return 6;
        } else if (mes.equalsIgnoreCase("julho")) {
            return 7;
        } else if (mes.equalsIgnoreCase("agosto")) {
            return 8;
        } else if (mes.equalsIgnoreCase("setembro")) {
            return 9;
        } else if (mes.equalsIgnoreCase("outubro")) {
            return 10;
        } else if (mes.equalsIgnoreCase("novembro")) {
            return 11;
        } else {
            return mes.equalsIgnoreCase("dezembro") ? 12 : null;
        }
    }

    public static Date getVersaoJatoDate(Integer id_104) {
        Calendar calCongigurar = new GregorianCalendar();
        calCongigurar.set(1, Integer.parseInt(id_104.toString().substring(0, 4)));
        calCongigurar.set(2, Integer.parseInt(id_104.toString().substring(4, 6)) - 1);
        calCongigurar.set(5, Integer.parseInt(id_104.toString().substring(6, 8)));
        return truncateDia(calCongigurar.getTime());
    }

    public static Integer getJatoVersaoDate(Date id_104) {
        Calendar calCongigurar = new GregorianCalendar();
        calCongigurar.setTime(id_104);
        StringBuffer buf = new StringBuffer();
        buf.append(calCongigurar.get(1));
        buf.append(calCongigurar.get(2));
        buf.append(calCongigurar.get(5));
        return Integer.parseInt(buf.toString());
    }

    public static String getTempoValido(int horasmais) {
        Calendar calCongigurar = new GregorianCalendar();
        calCongigurar.setTime(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy HHmmss");
        calCongigurar.add(10, horasmais);
        return sdf.format(calCongigurar.getTime());
    }

    public static boolean getComparaDateHora(int dia, int mes, int ano, int hora, int min, int seg) {
        Calendar calCongigurar = new GregorianCalendar();
        calCongigurar.setTime(new Date());
        calCongigurar.set(5, dia);
        calCongigurar.set(2, mes - 1);
        calCongigurar.set(1, ano);
        calCongigurar.set(11, hora);
        calCongigurar.set(12, min);
        calCongigurar.set(13, seg);
        return calCongigurar.getTime().getTime() > (new Date()).getTime();
    }

    public static Date getPrimeiroDiaMesIndicado(Integer mes, Integer ano) {
        Calendar calDate = new GregorianCalendar(ano, mes, 1);
        return getPrimeiroDiaMes(calDate.getTime());
    }

    public static Date getUltimoDiaMesIndicado(Integer mes, Integer ano) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(getPrimeiroDiaMesIndicado(mes, ano));
        cal.add(2, 1);
        cal.add(5, -1);
        truncateDia((Calendar)cal);
        return cal.getTime();
    }

    public static boolean isEntreFaixaDatas(Date dataInicial, Date dataFinal) {
        Calendar calDate = new GregorianCalendar();
        Calendar calDateInicial = new GregorianCalendar();
        Calendar calDateFinal = new GregorianCalendar();
        calDate.setTime(new Date());
        calDateInicial.setTime(dataInicial);
        calDateFinal.setTime(dataFinal);
        truncateDia((Calendar)calDate);
        truncateDia((Calendar)calDateInicial);
        truncateDia((Calendar)calDateFinal);
        return calDateInicial.getTime().getTime() <= calDate.getTime().getTime() && calDateFinal.getTime().getTime() >= calDate.getTime().getTime();
    }

    public static void calculaPeriodoAnterior(Date inicioPeriodoAtual, Date fimPeriodoAtual, Date inicioPeriodoAnterior, Date fimPeriodoAnterior) {
        int numDias = getDiferencaDias(inicioPeriodoAtual, fimPeriodoAtual);
        numDias += 7 - numDias % 7;
        inicioPeriodoAnterior.setTime(getDataNDiasAFrente(inicioPeriodoAtual, -numDias).getTime());
        fimPeriodoAnterior.setTime(getDataNDiasAFrente(fimPeriodoAtual, -numDias).getTime());
    }

    public static String getDataString(Date date, String formatoIn, String formatoOut) {
        String data = "";

        try {
            if (date != null && date.toString().indexOf("-") != -1) {
                SimpleDateFormat in = new SimpleDateFormat(formatoIn);
                SimpleDateFormat out = new SimpleDateFormat(formatoOut);
                data = out.format(in.parse(date.toString()));
            }
        } catch (Exception var6) {
            var6.printStackTrace();
        }

        return data;
    }

    public static String getDataString(Date date, String formatoOut) {
        String data = "";

        try {
            SimpleDateFormat out = new SimpleDateFormat(formatoOut);
            data = out.format(date);
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return data;
    }

    public static String getYYYYDDMM(Date date) {
        String dia = "";
        String mes = "";
        String ano = "";

        try {
            Calendar cal = truncateDiaRetornaCalendar(date);
            dia = Integer.toString(cal.get(5));
            mes = Integer.toString(cal.get(2) + 1);
            ano = Integer.toString(cal.get(1));
            if (dia.length() == 1) {
                dia = "0" + dia;
            }

            if (mes.length() == 1) {
                mes = "0" + mes;
            }
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        return ano + "-" + mes + "-" + dia;
    }

    public static String getYYYYMMDD(Date date) {
        String dia = "";
        String mes = "";
        String ano = "";

        try {
            Calendar cal = truncateDiaRetornaCalendar(date);
            dia = Integer.toString(cal.get(5));
            mes = Integer.toString(cal.get(2) + 1);
            ano = Integer.toString(cal.get(1));
            if (dia.length() == 1) {
                dia = "0" + dia;
            }

            if (mes.length() == 1) {
                mes = "0" + mes;
            }
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        return ano + mes + dia;
    }

    public static String getDDMMYYYY(Date date) {
        String dia = "";
        String mes = "";
        String ano = "";

        try {
            Calendar cal = truncateDiaRetornaCalendar(date);
            dia = Integer.toString(cal.get(5));
            mes = Integer.toString(cal.get(2) + 1);
            ano = Integer.toString(cal.get(1));
            if (dia.length() == 1) {
                dia = "0" + dia;
            }

            if (mes.length() == 1) {
                mes = "0" + mes;
            }
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        return dia + mes + ano;
    }

    public static String getYYYYMM(Date date) {
        String mes = "";
        String ano = "";

        try {
            Calendar cal = truncateDiaRetornaCalendar(date);
            mes = Integer.toString(cal.get(2) + 1);
            ano = Integer.toString(cal.get(1));
            if (mes.length() == 1) {
                mes = "0" + mes;
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return ano + mes;
    }

    public static String getNivelAtividadeEmail(Date date) {
        String nivel = "";
        if (date != null) {
            int numDias = getDiferencaDias(date, new Date());
            if (numDias < 90) {
                nivel = "0";
            } else if (numDias >= 90 && numDias <= 270) {
                nivel = "1";
            } else if (numDias > 270) {
                nivel = "3";
            }
        }

        return nivel;
    }

    public static Date getFormatIntegerForDate(Integer data) {
        Calendar calCongigurar = new GregorianCalendar();
        calCongigurar.set(1, Integer.parseInt(data.toString().substring(0, 4)));
        calCongigurar.set(2, Integer.parseInt(data.toString().substring(4, 6)) - 1);
        calCongigurar.set(5, Integer.parseInt(data.toString().substring(6, 8)));
        return truncateDia(calCongigurar.getTime());
    }

    public static Date getStringToDateddmmyyy(String date) throws ParseException {
        SimpleDateFormat out = getddMMYYYYSdf();
        return out.parse(date);
    }

    public static Date getStringToDateyyyymmdd(String date) throws ParseException {
        SimpleDateFormat out = getYYYYMMddSdf();
        return out.parse(date);
    }

    public static String getIntegerForStringToDateddmmyyy(Integer data) {
        String formatDate = "";

        try {
            Calendar calCongigurar = new GregorianCalendar();
            calCongigurar.set(1, Integer.parseInt(data.toString().substring(0, 4)));
            calCongigurar.set(2, Integer.parseInt(data.toString().substring(4, 6)) - 1);
            calCongigurar.set(5, Integer.parseInt(data.toString().substring(6, 8)));
            SimpleDateFormat out = getddMMYYYYSdf();
            formatDate = out.format(truncateDia(calCongigurar.getTime()));
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return formatDate;
    }

    /** @deprecated */
    @Deprecated
    public static Date getCaixaMesAtualAnunciantePJ() {
        Date hoje = new Date();
        Date dataInicioMes = getPrimeiroDiaMes(hoje);
        if (hoje.before(getNDiasUteisAPartirDe(getUltimoDiaMesAnterior(), 3))) {
            dataInicioMes = getPrimeiroDiaMesAnterior();
        }

        return dataInicioMes;
    }

    public static Integer getDataBaseCobranca(Date dataAdesaoReativacao) {
        if (dataAdesaoReativacao == null) {
            dataAdesaoReativacao = new Date();
        }

        Calendar dataAdesaoReativacaoCal = Calendar.getInstance();
        dataAdesaoReativacaoCal.setTime(dataAdesaoReativacao);
        Integer diaBase = getDia(dataAdesaoReativacaoCal.getTime());
        return ajustaDiaBaseCobranca(diaBase);
    }

    public static Integer ajustaDiaBaseCobranca(Integer dia) {
        if (dia == null) {
            throw new IllegalArgumentException("DateHelper.ajustaDiaBaseCobranca - o dia da data base não pode ser nulo");
        } else {
            if (dia <= 9) {
                dia = 10;
            } else if (dia >= 10 && dia <= 16) {
                dia = 17;
            } else if (dia >= 20 && dia <= 24) {
                dia = 25;
            }

            return dia;
        }
    }

    public static boolean verificaDataReportsFaceBook(Long timeStart, Long timeStop) {
        Date dataInicial = new Date(timeStart * 1000L);
        Calendar d = truncateDiaRetornaCalendar(dataInicial);
        Date dataFinal = new Date(timeStop * 1000L);
        Calendar c = truncateDiaRetornaCalendar(dataFinal);
        int day = c.get(5);
        c.set(5, day - 1);
        return c.getTimeInMillis() == d.getTimeInMillis();
    }

    public static Date getDataEmAtraso(Date dataReferencia) {
        return getDataNDiasAFrente(dataReferencia, -1, true);
    }

    public static String getFormattedIntervalDate(Date date, boolean shortFormat) {
        if (date == null) {
            return null;
        } else {
            int diffSegundos = getDiferencaSegundos(date, new Date());
            if (diffSegundos < 60) {
                return "há " + diffSegundos + " segundo" + (diffSegundos > 1 ? "s" : "");
            } else {
                int diffMinutos = getDiferencaMinutos(date, new Date());
                if (diffMinutos < 60) {
                    return "há " + diffMinutos + " minuto" + (diffMinutos > 1 ? "s" : "");
                } else {
                    int diffHoras = getDiferencaHoras(date, new Date());
                    if (diffHoras < 24) {
                        return "há " + diffHoras + " hora" + (diffHoras > 1 ? "s" : "");
                    } else {
                        int diffDias = getDiferencaDias(date, new Date());
                        return "há " + diffDias + " dia" + (diffDias > 1 ? "s" : "");
                    }
                }
            }
        }
    }

    public static String getYYYYMMDDTHHMMSS(String date) throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        Date dataFormatada = format.parse(date);
        return output.format(dataFormatada);
    }

    public static String getFormattedSecondsToHMS(Integer seconds) {
        try {
            if (seconds != null && seconds >= 0) {
                int sec = seconds % 60;
                int minutes = seconds / 60;
                int minute = minutes % 60;
                int hour = minutes / 60;
                return String.format("%02d:%02d:%02d", hour, minute, sec);
            } else {
                return null;
            }
        } catch (Exception var5) {
            return "";
        }
    }

    public static SimpleDateFormat getSdf(String pattern) {
        return new SimpleDateFormat(pattern);
    }

    public static String format(String pattern, Date date) {
        return getSdf(pattern).format(date);
    }

    public static Date parse(String pattern, String date) {
        try {
            return getSdf(pattern).parse(date);
        } catch (ParseException var3) {
            return null;
        }
    }

    public static boolean isDiaUtil(Calendar cal) {
        if (feriados == null) {
            return true;
        } else if (cal.get(7) != 1 && cal.get(7) != 7) {
            truncateDia(cal);
            Date d = cal.getTime();
            return !feriados.contains(d);
        } else {
            return false;
        }
    }

    public static synchronized void setFeriados(Set<Date> _feriados) {
        feriados = _feriados;
    }

    static {
        GregorianCalendar a = new GregorianCalendar();
        a.setTime(new Date());
        ANO_ATUAL = a.get(1);
        MES_ATUAL = a.get(2);
    }
}