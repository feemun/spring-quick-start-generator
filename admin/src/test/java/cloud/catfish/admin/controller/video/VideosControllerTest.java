package cloud.catfish.admin.controller.video;

import cloud.catfish.admin.domain.vo.VideoFrameDetailVO;
import cloud.catfish.admin.domain.vo.VideoStatisticsVO;
import cloud.catfish.admin.service.VideosService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VideosController.class)
public class VideosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VideosService videosService;

    @Test
    public void testGetStatistics_Success() throws Exception {
        VideoStatisticsVO vo = new VideoStatisticsVO();
        vo.setVideoId(1L);
        vo.setVideoName("Test Video");
        vo.setFrameCount(100);
        vo.setAttachmentCount(5);
        vo.setOcrCount(10);
        vo.setFaceCount(2);

        when(videosService.getStatistics(1L)).thenReturn(vo);

        mockMvc.perform(get("/admin/videos/1/statistics")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.videoId").value(1))
                .andExpect(jsonPath("$.videoName").value("Test Video"))
                .andExpect(jsonPath("$.frameCount").value(100));
    }

    @Test
    public void testGetStatistics_NotFound() throws Exception {
        when(videosService.getStatistics(anyLong())).thenReturn(null);

        mockMvc.perform(get("/admin/videos/999/statistics")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetRecentFrames_Success() throws Exception {
        VideoFrameDetailVO frameVO = new VideoFrameDetailVO();
        
        when(videosService.getRecentFrames(1L, 10)).thenReturn(List.of(frameVO));

        mockMvc.perform(get("/admin/videos/1/frames/recent")
                .param("limit", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }
    
    @Test
    public void testGetRecentFrames_DefaultLimit() throws Exception {
        when(videosService.getRecentFrames(1L, 10)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/videos/1/frames/recent")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
