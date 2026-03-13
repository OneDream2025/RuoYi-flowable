<template>
  <div class="app-container">
    <!-- 高级筛选表单 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" label-width="80px" v-show="showSearch">
      <el-row>
        <el-col :span="6">
          <el-form-item label="用户名称" prop="userName">
            <el-input
              v-model="queryParams.userName"
              placeholder="请输入用户名称"
              clearable
              @keyup.enter.native="handleQuery"
              style="width: 200px"
            />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="登录地址" prop="ipaddr">
            <el-input
              v-model="queryParams.ipaddr"
              placeholder="请输入IP地址"
              clearable
              @keyup.enter.native="handleQuery"
              style="width: 200px"
            />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="部门" prop="deptId">
            <treeselect
              v-model="queryParams.deptId"
              :options="deptOptions"
              :show-count="true"
              placeholder="请选择部门"
              style="width: 200px"
            />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="浏览器" prop="browser">
            <el-select v-model="queryParams.browser" placeholder="请选择浏览器" clearable style="width: 200px">
              <el-option label="Chrome" value="Chrome" />
              <el-option label="Firefox" value="Firefox" />
              <el-option label="Safari" value="Safari" />
              <el-option label="Edge" value="Edge" />
              <el-option label="IE" value="IE" />
              <el-option label="Opera" value="Opera" />
              <el-option label="其他" value="其他" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="6">
          <el-form-item label="操作系统" prop="os">
            <el-select v-model="queryParams.os" placeholder="请选择操作系统" clearable style="width: 200px">
              <el-option label="Windows" value="Windows" />
              <el-option label="Mac OS" value="Mac OS" />
              <el-option label="Linux" value="Linux" />
              <el-option label="Android" value="Android" />
              <el-option label="iOS" value="iOS" />
              <el-option label="其他" value="其他" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="登录时间" prop="loginTimeRange">
            <el-date-picker
              v-model="queryParams.loginTimeRange"
              type="datetimerange"
              range-separator="至"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
              value-format="timestamp"
              style="width: 400px"
            />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
            <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <!-- 操作按钮 -->
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

    <!-- 数据表格 -->
    <el-table
      v-loading="loading"
      :data="list"
      @sort-change="handleSortChange"
      style="width: 100%;"
    >
      <el-table-column label="序号" type="index" align="center" width="50">
        <template slot-scope="scope">
          <span>{{(queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1}}</span>
        </template>
      </el-table-column>
      <el-table-column label="会话编号" align="center" prop="tokenId" :show-overflow-tooltip="true" width="280" />
      <el-table-column label="登录名称" align="center" prop="userName" sortable="custom" :show-overflow-tooltip="true" width="120" />
      <el-table-column label="部门名称" align="center" prop="deptName" sortable="custom" width="120" />
      <el-table-column label="主机" align="center" prop="ipaddr" sortable="custom" :show-overflow-tooltip="true" width="140" />
      <el-table-column label="登录地点" align="center" prop="loginLocation" :show-overflow-tooltip="true" width="120" />
      <el-table-column label="浏览器" align="center" prop="browser" width="100" />
      <el-table-column label="操作系统" align="center" prop="os" width="100" />
      <el-table-column label="登录时间" align="center" prop="loginTime" width="160" sortable="custom">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.loginTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="会话时长" align="center" prop="sessionDuration" width="120" sortable="custom">
        <template slot-scope="scope">
          <span>{{ formatDuration(scope.row.loginTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="80">
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

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
  </div>
</template>

<script>
import { list, forceLogout } from "@/api/monitor/online";
import { deptTreeSelect } from "@/api/system/user";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";

export default {
  name: "Online",
  components: { Treeselect },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 表格数据
      list: [],
      // 部门树选项
      deptOptions: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        ipaddr: undefined,
        userName: undefined,
        deptId: undefined,
        deptName: undefined,
        browser: undefined,
        os: undefined,
        loginTimeRange: [],
        loginTimeStart: undefined,
        loginTimeEnd: undefined,
        orderByColumn: undefined,
        isAsc: undefined
      }
    };
  },
  created() {
    this.getList();
    this.getDeptTree();
  },
  methods: {
    /** 查询在线用户列表 */
    getList() {
      this.loading = true;
      // 处理时间范围参数
      const params = this.buildQueryParams();
      
      list(params).then(response => {
        this.list = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 构建查询参数 */
    buildQueryParams() {
      const params = { ...this.queryParams };
      if (this.queryParams.loginTimeRange && this.queryParams.loginTimeRange.length === 2) {
        params.loginTimeStart = this.queryParams.loginTimeRange[0];
        params.loginTimeEnd = this.queryParams.loginTimeRange[1];
      }
      delete params.loginTimeRange;
      return params;
    },
    /** 查询部门下拉树结构 */
    getDeptTree() {
      deptTreeSelect().then(response => {
        this.deptOptions = response.data;
      });
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.queryParams.loginTimeRange = [];
      this.handleQuery();
    },
    /** 排序变化处理 */
    handleSortChange({ column, prop, order }) {
      if (order) {
        this.queryParams.orderByColumn = prop;
        this.queryParams.isAsc = order === 'ascending' ? 'asc' : 'desc';
      } else {
        this.queryParams.orderByColumn = undefined;
        this.queryParams.isAsc = undefined;
      }
      this.getList();
    },
    /** 导出按钮操作 */
    handleExport() {
      const params = this.buildQueryParams();
      // 导出时不传分页参数
      delete params.pageNum;
      delete params.pageSize;
      
      this.download('monitor/online/export', params, `online_user_${new Date().getTime()}.xlsx`);
    },
    /** 强退按钮操作 */
    handleForceLogout(row) {
      this.$modal.confirm('是否确认强退名称为"' + row.userName + '"的用户？').then(function() {
        return forceLogout(row.tokenId);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("强退成功");
      }).catch(() => {});
    },
    /** 格式化会话时长 */
    formatDuration(loginTime) {
      if (!loginTime) return '-';
      const duration = Date.now() - loginTime;
      const hours = Math.floor(duration / (1000 * 60 * 60));
      const minutes = Math.floor((duration % (1000 * 60 * 60)) / (1000 * 60));
      if (hours > 0) {
        return `${hours}小时${minutes}分钟`;
      } else {
        return `${minutes}分钟`;
      }
    }
  }
};
</script>

<style scoped>
.mb8 {
  margin-bottom: 8px;
}
</style>
