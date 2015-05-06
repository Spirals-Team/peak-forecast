#!/bin/sh
vbox_home=`pwd`/`dirname $0`
vm_name=$1
vdi_file=$vbox_home/VDI/$(echo $vm_name | tr "[:upper:]" "[:lower:]").vdi
 
# Création du disque dur
echo "Creating virtual hard drive disk file ($vdi_file)"
cp $vbox_home/VDI/deb5-server.vdi $vdi_file
VBoxManage internalcommands sethduuid $vdi_file
echo "Opening virtual hard drive disk ($vdi_file)"
VBoxManage openmedium disk $vdi_file
 
# Création de la machine virtuelle
echo "Creating virtual machine $vm_name"
VBoxManage createvm --name $vm_name --basefolder $vbox_home/Machines/ --ostype Debian --register
VBoxManage modifyvm $vm_name --memory 512
VBoxManage modifyvm $vm_name --nic2 hostonly --hostonlyadapter2 vboxnet0
 
echo "Attaching hdd to the virtual machine"
VBoxManage storagectl $vm_name --name "Contrôleur IDE" --add ide
VBoxManage storageattach $vm_name --storagectl "Contrôleur IDE" --port 0 --device 0 --type hdd --medium $vdi_file
 
echo "Adding shared folders"
VBoxManage sharedfolder add $vm_name --name "stockage" --hostpath ~/stockage --readonly
VBoxManage sharedfolder add $vm_name --name "tmp" --hostpath ~/stockage/tmp
