package com.cetc.hubble.metagrid.service;

import com.cetc.hubble.metagrid.dao.RelationshipDAO;
import com.cetc.hubble.metagrid.vo.DataModelJson;
import com.cetc.hubble.metagrid.vo.DataModelRelationType;
import com.cetc.hubble.metagrid.vo.DataModelStatus;
import com.cetc.hubble.metagrid.vo.FrontModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by tao on 13/12/16.
 */
@Service
@Transactional
public class RelationshipService {

    @Autowired
    private RelationshipDAO rsDAO;
    @Autowired
    private DataModelService dataModelService;

    public List<DataModelRelationType> getRelationTypeLs() {

        return rsDAO.getRelationTypeLs();
    }

    public int save(DataModelJson json,String owner) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        FrontModel frontModel = mapper.readValue(json.getJson(),FrontModel.class);
        System.out.println(frontModel);
        int modelId = dataModelService.save(frontModel, json.getName(), owner,json.getType());
        json.setId(modelId);
        rsDAO.save(json);



        return modelId;
    }

    public int update(DataModelJson json,String owner) throws Exception {
        rsDAO.deleteAllOld(json.getId());
        int modelId = save(json, owner);
        return modelId;
    }

    public DataModelJson query(Integer id) {

        return rsDAO.query(id);
    }

    public List<DataModelStatus> getDMStatusLs() {

        return rsDAO.getDMStatusLs();
    }
}
