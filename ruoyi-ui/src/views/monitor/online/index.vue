<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="部门名称" prop="deptIdParam">
        <el-select
          v-model="queryParams.deptIdParam"
          placeholder="请选择部门"
          clearable
          style="width: 200px"
        >
          <el-option
            v-for="dept in flatDeptOptions"
            :key="dept.id"
            :label="dept.label"
            :value="dept.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="角色" prop="roleIdParam">
        <el-select
          v-model="queryParams.roleIdParam"
          placeholder="请选择角色"
          clearable
          style="width: 200px"
        >
          <el-option
            v-for="role in roleOptions"
            :key="role.roleId"
            :label="role.roleName"
            :value="role.roleId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="登录地址" prop="ipaddr">
        <el-input
          v-model="queryParams.ipaddr"
          placeholder="请输入登录地址(支持模糊搜索)"
          clearable
          style="width: 200px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="用户名称" prop="userName">
        <el-input
          v-model="queryParams.userName"
          placeholder="请输入用户名称(支持模糊搜索)"
          clearable
          style="width: 200px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="登录时间">
        <el-date-picker
          v-model="dateRange"
          style="width: 240px"
          value-format="yyyy-MM-dd"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        ></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['monitor:online:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table
      v-loading="loading"
      :data="list.slice((pageNum-1)*pageSize,pageNum*pageSize)"
      style="width: 100%;"
      @sort-change="handleSortChange"
    >
      <el-table-column label="序号" type="index" align="center">
        <template slot-scope="scope">
          <span>{{(pageNum - 1) * pageSize + scope.$index + 1}}</span>
        </template>
      </el-table-column>
      <el-table-column label="会话编号" align="center" prop="tokenId" :show-overflow-tooltip="true" />
      <el-table-column label="登录名称" align="center" prop="userName" :show-overflow-tooltip="true" sortable="custom" />
      <el-table-column label="部门名称" align="center" prop="deptName" />
      <el-table-column label="角色" align="center" prop="roleName" :show-overflow-tooltip="true" />
      <el-table-column label="主机" align="center" prop="ipaddr" :show-overflow-tooltip="true" sortable="custom" />
      <el-table-column label="登录地点" align="center" prop="loginLocation" :show-overflow-tooltip="true" />
      <el-table-column label="浏览器" align="center" prop="browser" />
      <el-table-column label="操作系统" align="center" prop="os" />
      <el-table-column label="登录时间" align="center" prop="loginTime" width="180" sortable="custom">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.loginTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="会话时长" align="center" prop="sessionDuration" sortable="custom">
        <template slot-scope="scope">
          <span>{{ formatDuration(scope.row.sessionDuration) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleForceLogout(scope.row)"
            v-hasPermi="['monitor:online:forceLogout']"
          >强退</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="pageNum" :limit.sync="pageSize" />
  </div>
</template>

<script>
import { list, forceLogout } from "@/api/monitor/online";
import { treeselect } from "@/api/system/dept";
import { optionselect } from "@/api/system/role";

export default {
  name: "Online",
  data() {
    return {
      loading: true,
      total: 0,
      list: [],
      pageNum: 1,
      pageSize: 10,
      showSearch: true,
      dateRange: [],
      deptOptions: [],
      flatDeptOptions: [],
      roleOptions: [],
      queryParams: {
        ipaddr: undefined,
        userName: undefined,
        deptIdParam: undefined,
        roleIdParam: undefined,
        orderByColumn: undefined,
        isAsc: undefined
      }
    };
  },
  created() {
    this.getList();
    this.getDeptTree();
    this.getRoleList();
  },
  methods: {
    getList() {
      this.loading = true;
      let params = { ...this.queryParams };
      if (this.dateRange && this.dateRange.length === 2) {
        params.params = {
          beginTime: this.dateRange[0],
          endTime: this.dateRange[1]
        };
      }
      list(params).then(response => {
        this.list = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    getDeptTree() {
      treeselect().then(response => {
        this.deptOptions = response.data;
        this.flatDeptOptions = this.flattenTree(response.data);
      });
    },
    flattenTree(tree, result = []) {
      if (!tree) return result;
      tree.forEach(node => {
        result.push({
          id: node.id,
          label: node.label
        });
        if (node.children && node.children.length > 0) {
          this.flattenTree(node.children, result);
        }
      });
      return result;
    },
    getRoleList() {
      optionselect().then(response => {
        this.roleOptions = response.data;
      });
    },
    handleQuery() {
      this.pageNum = 1;
      this.getList();
    },
    resetQuery() {
      this.dateRange = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    handleSortChange(column) {
      this.queryParams.orderByColumn = column.prop;
      this.queryParams.isAsc = column.order === 'ascending' ? 'asc' : 'desc';
      this.getList();
    },
    formatDuration(seconds) {
      if (!seconds) return '-';
      const hours = Math.floor(seconds / 3600);
      const minutes = Math.floor((seconds % 3600) / 60);
      const secs = Math.floor(seconds % 60);
      if (hours > 0) {
        return `${hours}时${minutes}分${secs}秒`;
      } else if (minutes > 0) {
        return `${minutes}分${secs}秒`;
      } else {
        return `${secs}秒`;
      }
    },
    handleForceLogout(row) {
      this.$modal.confirm('是否确认强退名称为"' + row.userName + '"的用户？').then(function() {
        return forceLogout(row.tokenId);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("强退成功");
      }).catch(() => {});
    },
    handleExport() {
      this.download('/monitor/online/export', {
        ...this.queryParams,
        params: this.dateRange && this.dateRange.length === 2 
          ? { beginTime: this.dateRange[0], endTime: this.dateRange[1] }
          : {}
      }, `online_${new Date().getTime()}.xlsx`);
    }
  }
};
</script>

