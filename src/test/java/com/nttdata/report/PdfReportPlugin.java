package com.nttdata.report;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import io.cucumber.plugin.EventListener;
import io.cucumber.plugin.event.EmbedEvent;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.PickleStepTestStep;
import io.cucumber.plugin.event.Status;
import io.cucumber.plugin.event.TestCaseFinished;
import io.cucumber.plugin.event.TestCaseStarted;
import io.cucumber.plugin.event.TestRunFinished;
import io.cucumber.plugin.event.TestStepFinished;
import io.cucumber.plugin.event.TestStepStarted;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PdfReportPlugin implements EventListener {

    // ===== Datos que puedes editar =====
    private static final String OUTPUT = "target/report/informe-ejecucion.pdf";
    private static final String AUTOR = "Francisco Alexander Sosa Ruiz";
    private static final String URL = "https://qalab.bensg.com/store";
    private static final Map<String, String> NOMBRES = Map.of(
            "@casoValido", "Caso 1: Usuario y contraseña válidos",
            "@loginInvalido", "Caso 2: Usuario y contraseña inválidos",
            "@categoriaInexistente", "Caso 3: Categoría inexistente");
    private static final Set<String> NEGATIVOS = Set.of("@loginInvalido", "@categoriaInexistente");

    // ===== Estilos =====
    private static final Font H1 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, new Color(20, 40, 80));
    private static final Font H2 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, new Color(20, 40, 80));
    private static final Font TXT = FontFactory.getFont(FontFactory.HELVETICA, 10);
    private static final Font BOLD = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
    private static final Font SMALL = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.GRAY);

    private static class StepData {
        String text;
        Status status = Status.SKIPPED;
        String error;
        final List<byte[]> images = new ArrayList<>();
    }

    private static class CaseData {
        String title;
        boolean negative;
        Status status;
        String error;
        long millis;
        final List<StepData> steps = new ArrayList<>();
        final List<byte[]> finalImages = new ArrayList<>();

        boolean passed() { return status == Status.PASSED; }
        boolean cumple() { return negative ? !passed() : passed(); }
        String esperado() { return negative ? "FAILED (caso negativo)" : "PASSED"; }
    }

    private final List<CaseData> cases = new ArrayList<>();
    private final LocalDateTime inicio = LocalDateTime.now();
    private CaseData current;
    private StepData currentStep;
    private boolean inHook;

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestCaseStarted.class, this::caseStarted);
        publisher.registerHandlerFor(TestStepStarted.class, this::stepStarted);
        publisher.registerHandlerFor(EmbedEvent.class, this::embed);
        publisher.registerHandlerFor(TestStepFinished.class, this::stepFinished);
        publisher.registerHandlerFor(TestCaseFinished.class, this::caseFinished);
        publisher.registerHandlerFor(TestRunFinished.class, e -> writePdf());
    }

    // ===== Captura de eventos =====
    private void caseStarted(TestCaseStarted e) {
        current = new CaseData();
        current.title = e.getTestCase().getName();
        for (String tag : e.getTestCase().getTags()) {
            if (NOMBRES.containsKey(tag)) current.title = NOMBRES.get(tag);
            if (NEGATIVOS.contains(tag)) current.negative = true;
        }
        currentStep = null;
        inHook = false;
    }

    private void stepStarted(TestStepStarted e) {
        if (e.getTestStep() instanceof PickleStepTestStep) {
            PickleStepTestStep ps = (PickleStepTestStep) e.getTestStep();
            currentStep = new StepData();
            currentStep.text = ps.getStep().getKeyword() + ps.getStep().getText();
            current.steps.add(currentStep);
            inHook = false;
        } else {
            inHook = true;
        }
    }

    private void embed(EmbedEvent e) {
        if (e.getMediaType() == null || !e.getMediaType().startsWith("image/")) return;
        if (inHook || currentStep == null) current.finalImages.add(e.getData());
        else currentStep.images.add(e.getData());
    }

    private void stepFinished(TestStepFinished e) {
        if (e.getTestStep() instanceof PickleStepTestStep && currentStep != null) {
            currentStep.status = e.getResult().getStatus();
            if (e.getResult().getError() != null) currentStep.error = resumir(e.getResult().getError());
        }
        inHook = false;
    }

    private void caseFinished(TestCaseFinished e) {
        current.status = e.getResult().getStatus();
        if (e.getResult().getError() != null) current.error = resumir(e.getResult().getError());
        current.millis = e.getResult().getDuration().toMillis();
        cases.add(current);
    }

    private static String resumir(Throwable t) {
        String msg = t.getMessage() == null ? t.getClass().getSimpleName() : t.getMessage();
        msg = msg.split("\\R")[0];
        return msg.length() > 250 ? msg.substring(0, 250) + "..." : msg;
    }

    // ===== Generación del PDF =====
    private void writePdf() {
        try {
            new File("target/report").mkdirs();
            Document doc = new Document(PageSize.A4, 40, 40, 50, 50);
            PdfWriter.getInstance(doc, new FileOutputStream(OUTPUT));
            doc.open();
            portada(doc);
            resumen(doc);
            for (CaseData c : cases) escenario(doc, c);
            hallazgos(doc);
            conclusiones(doc);
            doc.close();
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo generar el informe PDF", ex);
        }
    }

    private void portada(Document doc) throws Exception {
        doc.add(new Paragraph("Informe de Ejecución", H1));
        doc.add(new Paragraph("Automatización Web - Tienda en línea", H2));
        doc.add(Chunk.NEWLINE);
        PdfPTable t = new PdfPTable(new float[]{1, 2});
        t.setWidthPercentage(100);
        fila(t, "Aplicación bajo prueba", URL);
        fila(t, "Fecha de ejecución", inicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        fila(t, "Navegador", "Google Chrome");
        fila(t, "Herramientas", "Java 21, Selenium WebDriver, Cucumber, JUnit, Maven");
        fila(t, "Automatizador", AUTOR);
        doc.add(t);
    }

    private void resumen(Document doc) throws Exception {
        long cumplen = cases.stream().filter(CaseData::cumple).count();
        long passed = cases.stream().filter(CaseData::passed).count();
        doc.add(Chunk.NEWLINE);
        doc.add(new Paragraph("1. Resumen de la ejecución", H2));
        doc.add(new Paragraph("Escenarios ejecutados: " + cases.size()
                + "   |   Passed: " + passed
                + "   |   Failed: " + (cases.size() - passed)
                + "   |   Resultado esperado: " + cumplen + " de " + cases.size(), TXT));
        doc.add(Chunk.NEWLINE);

        PdfPTable t = new PdfPTable(new float[]{4, 2, 2, 2});
        t.setWidthPercentage(100);
        for (String h : new String[]{"Escenario", "Esperado", "Obtenido", "Conclusión"}) {
            t.addCell(celda(h, BOLD, new Color(230, 236, 245)));
        }
        for (CaseData c : cases) {
            t.addCell(celda(c.title, TXT, Color.WHITE));
            t.addCell(celda(c.esperado(), TXT, Color.WHITE));
            t.addCell(celda(String.valueOf(c.status), TXT, color(c.status)));
            t.addCell(celda(c.cumple() ? "Cumple" : "Revisar", BOLD,
                    c.cumple() ? new Color(198, 239, 206) : new Color(255, 199, 206)));
        }
        doc.add(t);
    }

    private void escenario(Document doc, CaseData c) throws Exception {
        doc.newPage();
        doc.add(new Paragraph("2. Resultado: " + c.title, H2));
        doc.add(new Paragraph("Resultado obtenido:  " + c.status + "   |   Esperado: " + c.esperado()
                + "   |   Duración: " + (c.millis / 1000.0) + " s", TXT));
        doc.add(Chunk.NEWLINE);

        PdfPTable t = new PdfPTable(new float[]{5, 1.5f});
        t.setWidthPercentage(100);
        t.addCell(celda("Paso", BOLD, new Color(230, 236, 245)));
        t.addCell(celda("Resultado", BOLD, new Color(230, 236, 245)));
        for (StepData s : c.steps) {
            t.addCell(celda(s.text, TXT, Color.WHITE));
            t.addCell(celda(String.valueOf(s.status), TXT, color(s.status)));
        }
        doc.add(t);

        if (c.error != null) {
            doc.add(Chunk.NEWLINE);
            doc.add(new Paragraph("Detalle del fallo: " + c.error, BOLD));
        }

        doc.add(Chunk.NEWLINE);
        doc.add(new Paragraph("Evidencia de la ejecución", H2));
        for (StepData s : c.steps) {
            for (byte[] img : s.images) imagen(doc, img, s.text);
        }
        for (byte[] img : c.finalImages) imagen(doc, img, "Captura al finalizar el escenario");
    }

    private void hallazgos(Document doc) throws Exception {
        doc.newPage();
        doc.add(new Paragraph("3. Hallazgos", H2));
        doc.add(Chunk.NEWLINE);
        for (CaseData c : cases) {
            if (!c.cumple()) {
                doc.add(new Paragraph("- [Revisar] " + c.title + ": resultado inesperado ("
                        + c.status + "). " + (c.error == null ? "" : c.error), TXT));
            } else if (c.negative) {
                doc.add(new Paragraph("- [Esperado] " + c.title
                        + ": la automatización falló como se esperaba. Motivo: " + c.error, TXT));
            }
        }
    }

    private void conclusiones(Document doc) throws Exception {
        long cumplen = cases.stream().filter(CaseData::cumple).count();
        boolean ok = cumplen == cases.size();
        doc.add(Chunk.NEWLINE);
        doc.add(new Paragraph("4. Conclusiones", H2));
        doc.add(Chunk.NEWLINE);
        doc.add(new Paragraph("Resultado final: " + (ok ? "APROBADO" : "REVISAR"), BOLD));
        doc.add(new Paragraph("Se ejecutaron " + cases.size() + " escenarios y " + cumplen
                + " dieron el resultado esperado. El flujo positivo valida autenticación, navegación por "
                + "categoría, confirmación del producto en el popup y cálculo de montos en popup y carrito. "
                + "Los casos negativos confirman que la automatización detecta credenciales inválidas y "
                + "categorías inexistentes, y detiene el flujo.", TXT));
    }

    // ===== Utilidades =====
    private static void fila(PdfPTable t, String a, String b) {
        t.addCell(celda(a, BOLD, new Color(230, 236, 245)));
        t.addCell(celda(b, TXT, Color.WHITE));
    }

    private static PdfPCell celda(String text, Font font, Color bg) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bg);
        cell.setPadding(5);
        return cell;
    }

    private static Color color(Status s) {
        if (s == Status.PASSED) return new Color(198, 239, 206);
        if (s == Status.FAILED) return new Color(255, 199, 206);
        return new Color(235, 235, 235);
    }

    private static void imagen(Document doc, byte[] data, String pie) throws Exception {
        Image img = Image.getInstance(data);
        img.scaleToFit(480, 260);
        img.setAlignment(Element.ALIGN_CENTER);
        doc.add(new Paragraph(pie, SMALL));
        doc.add(img);
    }
}