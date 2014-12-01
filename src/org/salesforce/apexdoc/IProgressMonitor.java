package org.salesforce.apexdoc;

public class IProgressMonitor {

    void worked(int work) {}
    void beginTask(String name, int totalWork) {}
    void done() {}
}
