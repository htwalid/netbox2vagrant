# netbox2vagrant

netbox2vagrant generates a topology.dot file with your data off NetBox, to help you to simulate a custom network topology.

You'll need the [Topology Converter](https://github.com/CumulusNetworks/topology_converter) to convert the generated topology file into a vagrant file.

Your settings will be saved to the config.json file and are set by default to the server "netbox". To get your API Token, take a look at NetBox -> Profiles -> API Tokens.

Tag Devices in NetBox with **"leaf, spine or mgmt"** to set their function in the topology.dot file and use the command line to change settings like memory or playbook.

`java -jar netbox2vagrant.jar -help`

will show you all the options