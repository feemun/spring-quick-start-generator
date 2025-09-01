package cloud.catfish.mbg.model.param;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

/**
 * Base class for all RequestParam classes providing common pagination and sorting functionality.
 * 
 * <p>This class contains standard pagination and sorting fields that are commonly used
 * across all request parameter classes:</p>
 * <ul>
 *   <li><strong>page</strong> - Page number for pagination (starting from 1)</li>
 *   <li><strong>size</strong> - Number of items per page</li>
 *   <li><strong>sort</strong> - Sort field and direction specification</li>
 * </ul>
 * 
 * <p>All generated RequestParam classes should extend this base class to inherit
 * these common pagination and sorting capabilities, eliminating code duplication
 * and ensuring consistency across all request parameter classes.</p>
 * 
 * <p><strong>Benefits of using BaseRequestParam:</strong></p>
 * <ul>
 *   <li>Eliminates code duplication across RequestParam classes</li>
 *   <li>Provides consistent pagination and sorting behavior</li>
 *   <li>Centralizes validation rules for common fields</li>
 *   <li>Simplifies maintenance and updates to pagination logic</li>
 * </ul>
 */
@Data
@Schema(description = "Base request parameters with pagination and sorting support")
public class BaseRequestParam {
    
    /**
     * Page number for pagination (starting from 1).
     */
    @Schema(description = "Page number (starting from 1)", example = "1")
    @Min(value = 1, message = "Page number must be greater than 0")
    private Integer page = 1;
    
    /**
     * Number of items per page.
     */
    @Schema(description = "Page size", example = "10")
    @Min(value = 1, message = "Page size must be greater than 0")
    @Max(value = 100, message = "Page size cannot exceed 100")
    private Integer size = 10;
    
    /**
     * Sort field and direction specification.
     * Format: 'fieldName,direction' (e.g., 'id,desc' or 'username,asc')
     */
    @Schema(description = "Sort field and direction (e.g., 'id,desc' or 'username,asc')")
    private String sort;
}