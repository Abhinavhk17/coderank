package com.coderank.api.domain;

public enum Language {
    PYTHON("py", "python"),
    JAVA("java", "java"),
    JAVASCRIPT("js", "node"),
    CPP("cpp", "g++");

    private final String fileExtension;
    private final String executor;

    Language(String fileExtension, String executor) {
        this.fileExtension = fileExtension;
        this.executor = executor;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public String getExecutor() {
        return executor;
    }
}

