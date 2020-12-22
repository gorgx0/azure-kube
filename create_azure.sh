az vm create -n kube01 -g kube01gorg --image UbuntuLTS --admin-username gorg --ssh-key-values azure_key.pub --plan-product B2S --no-wait -l eastus
az vm create -n kube02 -g kube01gorg --image UbuntuLTS --admin-username gorg --ssh-key-values azure_key.pub --plan-product B2S --no-wait -l eastus
az vm create -n kube03 -g kube01gorg --image UbuntuLTS --admin-username gorg --ssh-key-values azure_key.pub --plan-product B2S --no-wait -l westeurope
