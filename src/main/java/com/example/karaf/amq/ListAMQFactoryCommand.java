package com.example.karaf.amq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.management.StatsImpl;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.karaf.features.FeaturesService;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.osgi.framework.ServiceReference;

@Command(scope = "sample", name = "lsAMQFactory", description = "lists the registered ActiveMQ Connection Factory(s)")

public class ListAMQFactoryCommand extends OsgiCommandSupport {

    @Option(name = "-v", aliases = "-verbose", description = "Shows detailed output", required = false, multiValued = false)
    boolean verbose = false;

    @Option(name = "-a", aliases = "-all", description = "Shows all info", required = false, multiValued = false)
    private boolean all = false;

    protected Object doExecute() throws Exception {

        ServiceReference[] serviceReferences =
                getBundleContext().getAllServiceReferences(ActiveMQConnectionFactory.class.getName(), null);

        if (serviceReferences == null || serviceReferences.length == 0) {
            System.out.println("No ActiveMQConnectionFactory services found.");
            return null;
        }
        try {

            for (int i = 0; i < serviceReferences.length; i++) {

                ActiveMQConnectionFactory activeMQConnectionFactory =
                        (ActiveMQConnectionFactory) getBundleContext().getService(serviceReferences[i]);


                printDetails(activeMQConnectionFactory, serviceReferences[i]);
            }


        } finally {
            for (int i = 0; i < serviceReferences.length; i++) {

                try {
                    getBundleContext().ungetService(serviceReferences[i]);
                } catch (Exception ex) {
                    System.out.println("ungetService(): " + serviceReferences[i] + "threw exception: "
                            + ex.getMessage());
                }

            }

        }
        return null;
    }

    private void printDetails(ActiveMQConnectionFactory activeMQConnectionFactory, ServiceReference serviceReference) {

        if (activeMQConnectionFactory == null) {
            if (verbose) {
                System.out.println("activeMQConnectionFactory is NULL");
            }

            return;
        }

        System.out.println("ActiveMQ connection exported by bundle ID: "
                + serviceReference.getBundle().getBundleId()
                + " bundle location: "
                + serviceReference.getBundle().getLocation());

        String[] keys = serviceReference.getPropertyKeys();

        System.out.println("OSGi Service Properties");
        for (String key : keys) {

            System.out.println("--- " + key + " = " + serviceReference.getProperty(key));
        }


        System.out.println("Values set on ActiveMQConnectionFactory Object:");
        System.out.println("--- brokerURL: " + activeMQConnectionFactory.getBrokerURL());
        System.out.println("--- userName: " + activeMQConnectionFactory.getUserName());
        System.out.println("--- password: " + activeMQConnectionFactory.getPassword());
        System.out.println("--- clientID: " + activeMQConnectionFactory.getClientID());
        System.out.println("--- clientIDPrefix: " + activeMQConnectionFactory.getClientIDPrefix());

        if (all) {
            StatsImpl stats = activeMQConnectionFactory.getStats();
            String[] statsNames = stats.getStatisticNames();

            if (verbose) {
                if (statsNames == null || statsNames.length == 0) {
                    System.out.println("---- No Stats info available");
                }
            }

            for (String statName : statsNames) {
                System.out.println("---- stats: " + statName +
                        ", Description =" + stats.getStatistic(statName).getDescription() +
                        ", Start Time" + stats.getStatistic(statName).getStartTime() +
                        ", Last Sample Time" + stats.getStatistic(statName).getLastSampleTime());
            }
        }

        System.out.println("");


    }


}
