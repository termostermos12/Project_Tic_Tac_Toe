import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

public class GenerateProjectStructure {

    private static final String OUTPUT_FILE = "project_structure.txt";
    private static final Set<String> IGNORE_PATTERNS = new HashSet<>(Arrays.asList(
            ".git", ".gradle", "build", ".idea", "_pycache_",
            ".DS_Store", ".iml", "local.properties", ".externalNativeBuild",
            ".cxx", "captures", "gradle-wrapper.jar", "gradle-wrapper.properties"
    ));

    private static final Set<String> IGNORE_EXTENSIONS = new HashSet<>(Arrays.asList(
            ".class", ".jar", ".apk", ".dex", ".png", ".jpg", ".jpeg",
            ".gif", ".ico", ".svg", ".webp", ".so"
    ));

    public static void main(String[] args) {
        try {
            System.out.println("🚀 Génération de la structure du projet...");
            Path projectRoot = Paths.get(".");
            generateStructure(projectRoot);
            System.out.println("✅ Fichier '" + OUTPUT_FILE + "' généré avec succès!");
            System.out.println("📁 Emplacement: " + projectRoot.toAbsolutePath().resolve(OUTPUT_FILE));
        } catch (Exception e) {
            System.err.println("❌ Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void generateStructure(Path root) throws IOException {
        List<Path> allFiles = new ArrayList<>();

        // Collecter tous les fichiers
        try (Stream<Path> paths = Files.walk(root)) {
            paths.filter(Files::isRegularFile)
                    .filter(GenerateProjectStructure::shouldInclude)
                    .sorted()
                    .forEach(allFiles::add);
        }

        // Écrire dans le fichier de sortie
        try (BufferedWriter writer = Files.newBufferedWriter(
                Paths.get(OUTPUT_FILE), StandardCharsets.UTF_8)) {

            writeHeader(writer, root);
            writeTreeStructure(writer, allFiles, root);
            writeFileContents(writer, allFiles, root);
            writeFooter(writer, allFiles.size());
        }
    }

    private static boolean shouldInclude(Path path) {
        String pathStr = path.toString();
        String fileName = path.getFileName().toString();

        // Vérifier les patterns à ignorer
        for (String pattern : IGNORE_PATTERNS) {
            if (pathStr.contains(File.separator + pattern + File.separator) ||
                    pathStr.contains(File.separator + pattern) ||
                    fileName.equals(pattern)) {
                return false;
            }
        }

        // Vérifier les extensions à ignorer
        for (String ext : IGNORE_EXTENSIONS) {
            if (fileName.endsWith(ext)) {
                return false;
            }
        }

        // Ignorer le fichier de sortie lui-même
        if (fileName.equals(OUTPUT_FILE) || fileName.equals("GenerateProjectStructure.java")) {
            return false;
        }

        return true;
    }

    private static void writeHeader(BufferedWriter writer, Path root) throws IOException {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        writer.write("=".repeat(80) + "\n");
        writer.write("STRUCTURE DU PROJET: " + root.toAbsolutePath().getFileName() + "\n");
        writer.write("Généré le: " + timestamp + "\n");
        writer.write("=".repeat(80) + "\n\n");
    }

    private static void writeTreeStructure(BufferedWriter writer, List<Path> files, Path root)
            throws IOException {
        writer.write("📁 ARBORESCENCE DU PROJET\n");
        writer.write("-".repeat(80) + "\n\n");

        Set<Path> displayedDirs = new HashSet<>();

        for (Path file : files) {
            Path relativePath = root.relativize(file);
            int nameCount = relativePath.getNameCount();

            // Afficher la hiérarchie des dossiers
            for (int i = 0; i < nameCount - 1; i++) {
                Path dirPath = relativePath.subpath(0, i + 1);
                if (!displayedDirs.contains(dirPath)) {
                    String indent = "  ".repeat(i);
                    writer.write(indent + "📁 " + dirPath.getFileName() + "/\n");
                    displayedDirs.add(dirPath);
                }
            }

            // Afficher le fichier
            String indent = "  ".repeat(nameCount - 1);
            writer.write(indent + "📄 " + relativePath.getFileName() + "\n");
        }

        writer.write("\n" + "=".repeat(80) + "\n\n");
    }

    private static void writeFileContents(BufferedWriter writer, List<Path> files, Path root)
            throws IOException {
        writer.write("📝 CONTENU DES FICHIERS\n");
        writer.write("=".repeat(80) + "\n\n");

        for (Path file : files) {
            Path relativePath = root.relativize(file);

            writer.write("\n" + "▼".repeat(80) + "\n");
            writer.write("📂 Chemin: " + relativePath + "\n");
            writer.write("📍 Chemin complet: " + file.toAbsolutePath() + "\n");
            writer.write("▼".repeat(80) + "\n\n");

            try {
                String content = Files.readString(file, StandardCharsets.UTF_8);
                writer.write(content);
                if (!content.endsWith("\n")) {
                    writer.write("\n");
                }
            } catch (Exception e) {
                writer.write("[Erreur de lecture: " + e.getMessage() + "]\n");
            }

            writer.write("\n" + "▲".repeat(80) + "\n");
        }
    }

    private static void writeFooter(BufferedWriter writer, int fileCount) throws IOException {
        writer.write("\n" + "=".repeat(80) + "\n");
        writer.write("Total de fichiers documentés: " + fileCount + "\n");
        writer.write("=".repeat(80) + "\n");
    }
}