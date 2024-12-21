package mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import dto.CommentReportDTO;
import dto.UserReportDTO;

public interface UserReportMapper {

	List<UserReportDTO> selectAllReports();

	void insertReport(UserReportDTO dto);

	void updateReportStatus(@Param("reportNumber") int reportNumber, @Param("status") String status,
			@Param("adminId") int adminId);

	int insertCommentReport(CommentReportDTO dto);

	List<CommentReportDTO> selectCommentReports();

	boolean updateCommentReportStatus(Map<String, Object> map);
}
