function getGroupById(groups, id) {
    for(var i=0;i<groups.length;i++){
        if(groups[i].id == id){
            return groups[i];
        }
    }
}